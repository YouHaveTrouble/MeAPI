package me.youhavetrouble.meapi;

import com.sun.net.httpserver.HttpServer;
import me.youhavetrouble.meapi.discord.DiscordBot;
import me.youhavetrouble.meapi.endpoints.Endpoint;
import me.youhavetrouble.meapi.endpoints.OnlineEndpoint;
import me.youhavetrouble.meapi.endpoints.RootHandler;
import me.youhavetrouble.meapi.endpoints.games.FinalFantasyEndpoint;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class MeAPI {

    static int port = Integer.parseInt(System.getenv("APP_PORT"));

    private static DiscordBot discordBot;

    private static final HashSet<Endpoint> endpoints = new HashSet<>();
    private static final HashSet<Timer> timers = new HashSet<>();

    public static void main(String[] args) throws IOException {

        String discordBotKey = System.getenv("DISCORD_BOT_KEY");

        if (discordBotKey != null) {
            discordBot = new DiscordBot(discordBotKey);
            discordBot.start();
        }

        endpoints.add(new OnlineEndpoint());
        if (System.getenv("FFXIV_CHARACTER_ID") != null) endpoints.add(new FinalFantasyEndpoint());

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Server started at " + port);

        server.createContext("/", new RootHandler());

        endpoints.forEach(endpoint -> {
            server.createContext(endpoint.getId(), endpoint);
            Timer timer = new Timer(endpoint.getId());
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    endpoint.refreshData();
                }
            }, 2000, endpoint.refreshInterval());
            timers.add(timer);
        });

        server.setExecutor(null);
        server.start();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }
}
