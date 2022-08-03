package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.steam.SteamCrawler;
import me.youhavetrouble.meapi.steam.SteamStatus;
import net.dv8tion.jda.api.OnlineStatus;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class OnlineEndpoint implements Endpoint {

    private static OnlineStatus discordStatus;
    public static SteamStatus steamStatus;

    private final String discordUserTag;
    private final String steamId;

    public OnlineEndpoint() {
        discordUserTag = System.getenv("DISCORD_USER_TAG");
        steamId = System.getenv("STEAM_NAME");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        JSONObject object = new JSONObject();
        object.put("discord", discordStatus == null ? JSONObject.NULL : discordStatus);
        JSONObject steam = new JSONObject();
        steam.put("status", steamStatus.getStatus() == null ? JSONObject.NULL : steamStatus.getStatus());
        steam.put("game", steamStatus.getGame() == null ? JSONObject.NULL : steamStatus.getGame());
        object.put("steam", steam);
        String htmlResponse = object.toString();
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public String getId() {
        return "/online";
    }

    @Override
    public void refreshData() {
        discordStatus = MeAPI.getDiscordBot().getOnlineStatus(discordUserTag);
        steamStatus = SteamCrawler.getStatus(steamId);
    }

    @Override
    public int refreshInterval() {
        return 30 * 1000;
    }

}
