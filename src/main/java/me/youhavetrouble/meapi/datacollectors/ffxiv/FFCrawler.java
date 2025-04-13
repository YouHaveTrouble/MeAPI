package me.youhavetrouble.meapi.datacollectors.ffxiv;

import me.youhavetrouble.meapi.MeAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class FFCrawler {

    public static JSONObject getData(String characterId) {
        try {
            URL url = URI.create("https://na.finalfantasyxiv.com/lodestone/character/"+characterId).toURL();
            Connection connection = Jsoup.connect(url.toString());
            Document document = connection.get();

            JSONObject newData = new JSONObject();

            String datacenter = null;
            String server = null;

            Element worldElement = document.selectFirst(".frame__chara__world");
            if (worldElement != null) {
                String world = worldElement.ownText();
                String[] worldParts = world.split("\\[");
                if (worldParts.length == 2) {
                    datacenter = worldParts[0].trim();
                    server = worldParts[1].trim();
                } else {
                    MeAPI.logger.warning("Could not parse datacenter and server from world string: " + world);
                }
            }

            String name = document.getElementsByClass("frame__chara__name").text();

            newData.put("datacenter", datacenter == null ? JSONObject.NULL : datacenter);
            newData.put("server", server == null ? JSONObject.NULL : server);
            newData.put("name", name);

            JSONArray jobs = new JSONArray();

            Elements jobsList = document.select(".character__level__list ul li");
            for (Element listElement : jobsList) {
                Element imageElement = listElement.selectFirst("img");
                if (imageElement == null) continue;
                String jobName = imageElement.dataset().getOrDefault("tooltip", "Unknown");
                String jobLevel = listElement.ownText();
                int level = -1;
                try {
                    level = Integer.parseInt(jobLevel);
                } catch (NumberFormatException e) {
                    MeAPI.logger.warning("Could not parse job level: " + jobLevel);
                    continue;
                }
                JSONObject newJob = new JSONObject();
                newJob.put("name", jobName);
                newJob.put("level", level);
                jobs.put(newJob);
            }
            newData.put("jobs", jobs);

            Elements portraitElement = document.getElementsByClass("character__detail__image");
            if (portraitElement.isEmpty()) {
                MeAPI.logger.warning("Could not find character portrait element");
                newData.put("portrait_url", JSONObject.NULL);
            } else {
                String portraitUrl = portraitElement.select("a").attr("href");
                newData.put("portrait_url", portraitUrl);
            }

            return newData;
        } catch (IOException e) {
            MeAPI.logger.warning(String.format("Got an error while getting FF XIV character data: %s", e.getMessage()));
            return null;
        }
    }

}
