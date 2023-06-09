package me.youhavetrouble.meapi.datacollectors.riotgames;

import org.json.JSONObject;

public class SummonerData {
    private final JSONObject json;

    protected SummonerData(String id, String name, int level) {
        json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("level", level);
        json.put("live_game", JSONObject.NULL);
    }

    public String getId() {
        return json.getString("id");
    }

    public String getName() {
        return json.getString("name");
    }

    public void setLiveGame(JSONObject liveGame) {
        json.put("live_game", liveGame != null ? liveGame : JSONObject.NULL);
    }

    public String getAsJsonString() {
        return json.toString();
    }

}
