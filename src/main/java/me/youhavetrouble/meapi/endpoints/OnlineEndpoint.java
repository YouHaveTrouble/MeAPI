package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.datacollectors.steam.SteamCrawler;
import me.youhavetrouble.meapi.datacollectors.steam.SteamStatus;
import net.dv8tion.jda.api.OnlineStatus;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

public class OnlineEndpoint implements HttpHandler, TimedDataRefresh {

    private final String steamId;

    private String cachedResponse = null;

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
                cachedResponse = object.toString();
            }
        };
    }

    @Override
    public int refreshInterval() {
        return 30 * 1000;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (HttpExchange exchange = httpExchange) {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            if (cachedResponse == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, cachedResponse.length());
            exchange.getResponseBody().write(cachedResponse.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            MeAPI.logger.warning("Error handling root endpoint: " + e.getMessage());
        }
    }
}
