package steam;

import endpoints.OnlineHandler;
import endpoints.SteamHandler;
import endpoints.TestHandler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.TimerTask;

public class GetSteamDataTask extends TimerTask {

    String steamId;

    GetSteamDataTask(String steamId) {
        this.steamId = steamId;
    }

    @Override
    public void run() {
        try {
            URI uri = URI.create("https://steamcommunity.com/id/"+steamId);
            Connection connection = Jsoup.connect(uri.toString());
            Document document = connection.get();
            TestHandler.html = document.html();
            Elements onlineStatusElement = document.getElementsByClass("profile_in_game_header");
            OnlineHandler.steamStatus = SteamOnlineStatus.statusFromString(onlineStatusElement.get(0).text());
            Elements gameElement = document.getElementsByClass("profile_in_game_name");
            if (gameElement.isEmpty()) {
                SteamHandler.gameName = null;
            } else {
                SteamHandler.gameName = gameElement.get(0).text();
            }
        } catch (IOException ignored) {

        }
    }
}
