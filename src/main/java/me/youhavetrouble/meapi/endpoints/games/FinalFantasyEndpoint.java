package me.youhavetrouble.meapi.endpoints.games;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.datacollectors.ffxiv.FFCrawler;
import me.youhavetrouble.meapi.endpoints.TimedDataRefresh;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

public class FinalFantasyEndpoint implements HttpHandler, TimedDataRefresh {

    private final String characterId;
    private String ffxivData = null;

    public FinalFantasyEndpoint() {
        characterId = MeAPI.getEnvValue("FFXIV_CHARACTER_ID");
    }

    @Override
    public TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (characterId == null) return;
                JSONObject newData = FFCrawler.getData(characterId);
                if (newData == null) return;
                ffxivData = newData.toString();
            }
        };
    }

    public int refreshInterval() {
        return 60 * 60 * 1000; // 1 hour
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try (HttpExchange exchange = httpExchange) {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            if (ffxivData == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, ffxivData.length());
            exchange.getResponseBody().write(ffxivData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            MeAPI.logger.warn("Error handling FFXIV endpoint: {}", e.getMessage());
        }

    }

}
