package me.youhavetrouble.meapi.datacollectors.ffxiv;

import me.youhavetrouble.meapi.MeAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class FFCrawler {

    public static JSONObject getData(String characterId) {
        try {
            URL url = new URL("https://xivapi.com/character/"+characterId);
            Scanner scanner = new Scanner(url.openStream());
            JSONTokener tokener = new JSONTokener(scanner.useDelimiter("\\Z").next());
            JSONObject jsonObject = new JSONObject(tokener);

            JSONObject newData = new JSONObject();

            JSONObject character = jsonObject.getJSONObject("Character");
            newData.put("datacenter", character.get("DC"));
            newData.put("server", character.get("Server"));
            newData.put("name", character.get("Name"));

            JSONArray jobs = new JSONArray();

            JSONArray jobsArray = character.getJSONArray("ClassJobs");
            for (Object jobObject : jobsArray) {
                JSONObject job = (JSONObject) jobObject;
                JSONObject newJob = new JSONObject();
                newJob.put("name", job.get("Name"));
                newJob.put("level", job.get("Level"));
                jobs.put(newJob);
            }
            newData.put("jobs", jobs);
            newData.put("portrait_url", character.get("Portrait"));

            return newData;
        } catch (IOException e) {
            MeAPI.logger.warning(String.format("Got an error while getting FF XIV character data: %s", e.getMessage()));
            return null;
        }
    }

}
