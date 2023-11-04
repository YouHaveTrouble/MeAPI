package me.youhavetrouble.meapi;

import io.github.cdimascio.dotenv.Dotenv;
import me.youhavetrouble.jankwebserver.JankWebServer;
import me.youhavetrouble.meapi.datacollectors.discord.DiscordBot;
import me.youhavetrouble.meapi.endpoints.OnlineEndpoint;
import me.youhavetrouble.meapi.endpoints.RootEndpoint;
import me.youhavetrouble.meapi.endpoints.games.FinalFantasyEndpoint;
import me.youhavetrouble.meapi.endpoints.games.LeagueOfLegendsEndpoint;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MeAPI {

    public static Logger logger = Logger.getLogger("MeAPI");
    private static final Dotenv env = Dotenv.configure().ignoreIfMissing().load();
    static int port = getEnvValue("APP_PORT") == null ? 80 : Integer.parseInt(getEnvValue("APP_PORT"));
    private static DiscordBot discordBot;

    public static void main(String[] args) throws IOException {

        for (String arg : args) {
            if (arg.startsWith("port=")) {
                arg = arg.replaceFirst("port=", "");
                try {
                    port = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    logger.severe(String.format("Could not parse port number from arg port=%s", arg));
                    System.exit(1);
                }
            }
        }

        JankWebServer webServer = JankWebServer.create(port, Executors.newVirtualThreadPerTaskExecutor());

        String discordBotKey = getEnvValue("DISCORD_BOT_KEY");
        if (discordBotKey != null) {
            discordBot = new DiscordBot(discordBotKey);
            discordBot.start();
        }

        webServer.registerEndpoint(new RootEndpoint());

        OnlineEndpoint onlineEndpoint = new OnlineEndpoint();
        Timer onlineEndpointTimer = new Timer("onlineEndpoint", true);
        onlineEndpointTimer.scheduleAtFixedRate(onlineEndpoint.getTimerTask(), 2000, onlineEndpoint.refreshInterval());
        webServer.registerEndpoint(onlineEndpoint);

        FinalFantasyEndpoint finalFantasyEndpoint = new FinalFantasyEndpoint();
        Timer finalFantasyEndpointTimer = new Timer("ffEndpointTimer", true);
        finalFantasyEndpointTimer.scheduleAtFixedRate(finalFantasyEndpoint.getTimerTask(), 0, finalFantasyEndpoint.refreshInterval());
        webServer.registerEndpoint(finalFantasyEndpoint);

        LeagueOfLegendsEndpoint leagueOfLegendsEndpoint = new LeagueOfLegendsEndpoint();
        Timer leagueOfLegendsEndpointTimer = new Timer("lolEndpointTimer", true);
        leagueOfLegendsEndpointTimer.scheduleAtFixedRate(leagueOfLegendsEndpoint.getTimerTask(), 0, leagueOfLegendsEndpoint.refreshInterval());
        webServer.registerEndpoint(leagueOfLegendsEndpoint);

        webServer.start();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }

    public static String getEnvValue(String string) {
        return System.getenv(string) != null ? System.getenv(string) : env.get(string);
    }
}
