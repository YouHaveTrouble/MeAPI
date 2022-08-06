package me.youhavetrouble.meapi.endpoints.games;

import com.sun.net.httpserver.HttpExchange;
import me.youhavetrouble.meapi.MeAPI;
import me.youhavetrouble.meapi.endpoints.Endpoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;

public class FinalFantasyEndpoint implements Endpoint {

    private final String characterId;
    private String ffxivData = "{}";

    public FinalFantasyEndpoint() {
        characterId = MeAPI.getEnvValue("FFXIV_CHARACTER_ID");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String htmlResponse = ffxivData;
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public String getId() {
        return "/games/ffxiv";
    }

    @Override
    public void refreshData() {
        this.ffxivData = getData(characterId).toString();
    }

    @Override
    public int refreshInterval() {
        return 30 * 1000;
    }

    private JSONObject getData(String characterId) {
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
            System.out.println(e.getMessage());
            return new JSONObject();
        }
    }
}
