package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import me.youhavetrouble.jankwebserver.response.JsonResponse;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.datacollectors.steam.SteamCrawler;
import me.youhavetrouble.meapi.datacollectors.steam.SteamStatus;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.TimerTask;

public class OnlineEndpoint implements Endpoint, TimedDataRefresh {

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
                if (MeAPI.getDiscordBot().getJda() != null) {
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
    public String path() {
        return "/online";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI uri, @NotNull Headers headers, @NotNull Map<String, String> map, @Nullable String s) {
        if (requestMethod != RequestMethod.GET) return JsonResponse.create("{}", 405);
        if (cachedResponse == null) return JsonResponse.create("{}", 500);
        return JsonResponse.create(cachedResponse, 200);
    }
}
