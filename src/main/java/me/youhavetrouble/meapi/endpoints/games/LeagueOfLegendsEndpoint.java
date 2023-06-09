package me.youhavetrouble.meapi.endpoints.games;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import me.youhavetrouble.jankwebserver.response.JsonResponse;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.datacollectors.riotgames.OpggCrawler;
import me.youhavetrouble.meapi.datacollectors.riotgames.SummonerData;
import me.youhavetrouble.meapi.endpoints.TimedDataRefresh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.TimerTask;

public class LeagueOfLegendsEndpoint implements Endpoint, TimedDataRefresh {

    private final String riotRegion;

    private String summonerId = null;
    private String summonerName;

    private SummonerData summonerData = null;


    public LeagueOfLegendsEndpoint() {
        riotRegion = MeAPI.getEnvValue("LOL_REGION");
        summonerName = MeAPI.getEnvValue("LOL_SUMMONER_NAME");
    }

    @Override
    public String path() {
        return "/games/lol";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI uri, @NotNull Headers headers, @NotNull Map<String, String> map, @Nullable String s) {
        if (requestMethod != RequestMethod.GET) return JsonResponse.create("{}", 405);
        if (summonerData == null) return JsonResponse.create("{}", 404);
        return JsonResponse.create(summonerData.getAsJsonString(), 200);
    }

    @Override
    public TimerTask getTimerTask() {
        return new TimerTask() {

            int i = 0;
            @Override
            public void run() {
                if (summonerName == null || riotRegion == null) return;
                if (i >= 5) i = 0;
                if (i++ == 0) summonerData = OpggCrawler.getSummonerData(summonerName, riotRegion);
                if (summonerData != null) summonerId = summonerData.getId();
                if (summonerId == null) return;
                JSONObject liveGameData = OpggCrawler.getLiveGameData(summonerId, riotRegion);
                summonerData.setLiveGame(liveGameData);
            }
        };
    }

    @Override
    public int refreshInterval() {
        return 60000;
    }
}
