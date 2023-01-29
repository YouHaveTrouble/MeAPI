package me.youhavetrouble.meapi.endpoints.games;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import me.youhavetrouble.jankwebserver.response.JsonResponse;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.datacollectors.ffxiv.FFCrawler;
import me.youhavetrouble.meapi.endpoints.TimedDataRefresh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.TimerTask;

public class FinalFantasyEndpoint implements Endpoint, TimedDataRefresh {

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
                if (newData != null) ffxivData = newData.toString();
            }
        };
    }

    public int refreshInterval() {
        return 30 * 1000;
    }

    @Override
    public String path() {
        return "/games/ffxiv";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI uri, @NotNull Headers headers, @NotNull Map<String, String> map, @Nullable String s) {
        if (requestMethod != RequestMethod.GET) return JsonResponse.create("{}", 405);
        if (ffxivData == null) return JsonResponse.create("{}", 500);
        return JsonResponse.create(ffxivData, 200);
    }
}
