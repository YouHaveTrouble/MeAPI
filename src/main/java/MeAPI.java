import com.sun.net.httpserver.HttpServer;
import discord.DiscordBot;
import endpoints.OnlineHandler;
import endpoints.RootHandler;
import endpoints.SteamHandler;
import steam.SteamCrawler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MeAPI {

    static int port = 6660;

    private static DiscordBot discordBot;

    public static void main(String[] args) throws IOException {

        String discordBotKey = System.getenv("DISCORD_BOT_KEY");
        String discordUserId = System.getenv("DISCORD_USER_ID");

        if (discordBotKey != null) {
            discordBot = new DiscordBot(discordBotKey, discordUserId);
            discordBot.start();
        }

        String steamId = System.getenv("STEAM_NAME");
        if (steamId != null) new SteamCrawler(steamId);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Server started at " + port);
        server.createContext("/", new RootHandler());
        server.createContext("/online", new OnlineHandler());
        server.createContext("/steam", new SteamHandler());
        server.setExecutor(null);
        server.start();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }
}
