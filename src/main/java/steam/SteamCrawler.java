package steam;

import java.util.Timer;

public class SteamCrawler {

    public SteamCrawler(String steamId) {
        Timer timer = new Timer("OnlineStatusTimer", true);
        timer.scheduleAtFixedRate(new GetSteamDataTask(steamId), 1000, 1000*30);
    }

}
