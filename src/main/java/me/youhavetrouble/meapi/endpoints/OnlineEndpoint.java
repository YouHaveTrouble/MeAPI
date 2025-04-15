package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.datacollectors.steam.SteamCrawler;
import me.youhavetrouble.meapi.datacollectors.steam.SteamStatus;
import net.dv8tion.jda.api.OnlineStatus;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OnlineEndpoint implements HttpHandler, TimedDataRefresh {

    private final String steamId;

    private String cachedResponse = null;

    private final Map<UUID, LinkedBlockingQueue<String>> sseClients = new ConcurrentHashMap<>();

    public OnlineEndpoint() {
        steamId = MeAPI.getEnvValue("STEAM_NAME");
    }

    @Override
    public TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                OnlineStatus discordStatus;
                if (MeAPI.getDiscordBot() != null && MeAPI.getDiscordBot().getJda() != null) {
                    discordStatus = MeAPI.getDiscordBot().getOnlineStatus();
                } else {
                    discordStatus = OnlineStatus.OFFLINE;
                }
                SteamStatus steamStatus = SteamCrawler.getStatus(steamId);

                JSONObject object = new JSONObject();
                object.put("discord", discordStatus);
                JSONObject steam = new JSONObject();
                steam.put("status", steamStatus.getStatus() == null ? JSONObject.NULL : steamStatus.getStatus());
                steam.put("game", steamStatus.getGame() == null ? JSONObject.NULL : steamStatus.getGame());
                object.put("steam", steam);

                String newCachedResponse = object.toString();

                // only update if the response has changed
                if (newCachedResponse.equals(cachedResponse)) return;

                cachedResponse = newCachedResponse;
                sseClients.values().forEach(queue -> {
                    try {
                        queue.put(cachedResponse);
                    } catch (InterruptedException ignored) {}
                });

            }
        };
    }

    @Override
    public int refreshInterval() {
        return 30 * 1000;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        UUID clientId = UUID.randomUUID();
        try (HttpExchange exchange = httpExchange) {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            String acceptHeader = exchange.getRequestHeaders().getFirst("Accept");

            if (cachedResponse == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            // Check if the request is for SSE
            if (acceptHeader != null && acceptHeader.equals("text/event-stream")) {
                LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
                sseClients.put(clientId, queue); // Store the client queue
                MeAPI.logger.info("SSE client connected: " + clientId);
                exchange.getResponseHeaders().set("Content-Type", "text/event-stream; charset=UTF-8");
                exchange.getResponseHeaders().set("Cache-Control", "no-cache");
                exchange.sendResponseHeaders(200, 0);

                while (sseClients.containsKey(clientId)) {
                    exchange.getResponseBody().write(": heartbeat\n\n".getBytes(StandardCharsets.UTF_8));
                    exchange.getResponseBody().flush();

                    String update = queue.poll(5, TimeUnit.SECONDS); // Block until a new update is available
                    if (update == null) continue; // No update available, continue to the next iteration
                    String sseMessage = "data: " + update + "\n\n";
                    exchange.getResponseBody().write(sseMessage.getBytes(StandardCharsets.UTF_8));
                    exchange.getResponseBody().flush();
                }
            }

            // Handle regular JSON response
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, cachedResponse.length());
            exchange.getResponseBody().write(cachedResponse.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            MeAPI.logger.warning("Error handling root endpoint: " + e.getMessage() + " " + e.getCause().getClass().getName());
        } finally {
            if (sseClients.remove(clientId) != null) {
                // Remove the sse client queue
                MeAPI.logger.info("SSE client disconnected: " + clientId);
            }
        }
    }
}
