package me.youhavetrouble.meapi.datacollectors.steam;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;

public class SteamCrawler {

    public static SteamStatus getStatus(String steamId) {
        try {
            URI uri = URI.create("https://steamcommunity.com/id/" + steamId);
            Connection connection = Jsoup.connect(uri.toString());
            Document document = connection.get();
            Elements onlineStatusElement = document.getElementsByClass("profile_in_game_header");
            Elements gameElement = document.getElementsByClass("profile_in_game_name");
            if (onlineStatusElement.isEmpty()) {
                return new SteamStatus(
                        SteamOnlineStatus.OFFLINE,
                        null
                );
            }
            return new SteamStatus(
                    SteamOnlineStatus.statusFromString(onlineStatusElement.get(0).text()),
                    gameElement.isEmpty() ? null : gameElement.get(0).text()
            );
        } catch (IOException ignored) {
            return new SteamStatus(
                    SteamOnlineStatus.OFFLINE,
                    null
            );
        }
    }

}
