package me.youhavetrouble.meapi.datacollectors.riotgames;

import me.youhavetrouble.meapi.MeAPI;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class OpggCrawler {

    @Nullable
    public static SummonerData getSummonerData(String summonerName, String region) {

        try {
            URI uri = URI.create(String.format("https://www.op.gg/summoners/%s/%s/ingame", region, summonerName));
            Connection connection = Jsoup.connect(uri.toString());
            Document document = connection.get();
            Element dataElement = document.getElementById("__NEXT_DATA__");
            if (dataElement == null) return null;
            JSONObject data = new JSONObject(dataElement.data());
            JSONObject summoner = data.getJSONObject("props").getJSONObject("pageProps").getJSONObject("data");
            return new SummonerData(
                    summoner.getString("summoner_id"),
                    summoner.getString("name"),
                    summoner.getInt("level")
            );
        } catch (IOException e) {
            MeAPI.logger.warning(String.format("Got an error while getting riot summoner data: %s", e.getMessage()));
            return null;
        }
    }

    @Nullable
    public static JSONObject getLiveGameData(String summonerId, String region) {
        try {
            URL url = new URI("https://op.gg/api/v1.0/internal/bypass/spectates/eune/qM1jsb88CNW35VlaseSZM-VhQcXgcPoX3ok889Z6yb9qlZ4?hl=en_US").toURL();
            Scanner scanner = new Scanner(url.openStream());
            JSONTokener tokener = new JSONTokener(scanner.useDelimiter("\\Z").next());
            JSONObject jsonObject = new JSONObject(tokener).getJSONObject("data");

            JSONObject data = new JSONObject();
            data.put("game_id", jsonObject.getString("game_id"));
            data.put("map", jsonObject.getString("game_map"));
            data.put("game_type", jsonObject.getJSONObject("queue_info").getString("game_type"));
            data.put("started_at", jsonObject.getString("created_at"));
            return data;

        } catch (IOException | URISyntaxException e) {
            return null;
        }
    }

}
