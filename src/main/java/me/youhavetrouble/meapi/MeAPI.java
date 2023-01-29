package me.youhavetrouble.meapi;

import io.github.cdimascio.dotenv.Dotenv;
import me.youhavetrouble.jankwebserver.JankWebServer;
import me.youhavetrouble.meapi.datacollectors.discord.DiscordBot;
import me.youhavetrouble.meapi.endpoints.OnlineEndpoint;
import me.youhavetrouble.meapi.endpoints.RootEndpoint;
import me.youhavetrouble.meapi.endpoints.games.FinalFantasyEndpoint;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Logger;

public class MeAPI {

    public static Logger logger = Logger.getLogger("MeAPI");
    private static final Dotenv env = Dotenv.load();
    static int port = Integer.parseInt(getEnvValue("APP_PORT"));
    private static DiscordBot discordBot;

    public static void main(String[] args) throws IOException {

        JankWebServer webServer = JankWebServer.create(port, 16);

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

        webServer.start();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }

    public static String getEnvValue(String string) {
        return System.getenv(string) != null ? System.getenv(string) : env.get(string);
    }
}
