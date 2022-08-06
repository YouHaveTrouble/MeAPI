package me.youhavetrouble.meapi;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
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

    private static final Dotenv env = Dotenv.load();

    static int port = Integer.parseInt(getEnvValue("APP_PORT"));

    private static DiscordBot discordBot;

    private static final HashSet<Endpoint> endpoints = new HashSet<>();
    private static final HashSet<Timer> timers = new HashSet<>();

    public static void main(String[] args) throws IOException {

        String discordBotKey = getEnvValue("DISCORD_BOT_KEY");

        if (discordBotKey != null) {
            discordBot = new DiscordBot(discordBotKey);
            discordBot.start();
        }

        endpoints.add(new OnlineEndpoint());
        if (getEnvValue("FFXIV_CHARACTER_ID") != null) endpoints.add(new FinalFantasyEndpoint());

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
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
        System.out.println("Server started at " + port);
        server.start();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }

    public static String getEnvValue(String string) {
        return System.getenv(string) != null ? System.getenv(string) : env.get(string);
    }
}
