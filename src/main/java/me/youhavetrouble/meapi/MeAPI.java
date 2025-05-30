package me.youhavetrouble.meapi;

import io.github.cdimascio.dotenv.Dotenv;
import me.youhavetrouble.meapi.datacollectors.discord.DiscordBot;
import me.youhavetrouble.meapi.endpoints.OnlineEndpoint;
import me.youhavetrouble.meapi.endpoints.RootEndpoint;
import me.youhavetrouble.meapi.endpoints.games.FinalFantasyEndpoint;
import me.youhavetrouble.meapi.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;


public class MeAPI {

    public static Logger logger = LoggerFactory.getLogger("Server");
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
                    logger.error("Could not parse port number from arg port={}", arg);
                    System.exit(1);
                }
            }
        }

        WebServer webServer = new WebServer(port);

        String discordBotKey = getEnvValue("DISCORD_BOT_KEY");
        if (discordBotKey != null) {
            discordBot = new DiscordBot(discordBotKey);
            discordBot.start();
        }

        webServer.registerEndpoint("/", new RootEndpoint());

        OnlineEndpoint onlineEndpoint = new OnlineEndpoint();
        Timer onlineEndpointTimer = new Timer("onlineEndpoint", true);
        onlineEndpointTimer.scheduleAtFixedRate(onlineEndpoint.getTimerTask(), 2000, onlineEndpoint.refreshInterval());
        webServer.registerEndpoint("/online", onlineEndpoint);

        FinalFantasyEndpoint finalFantasyEndpoint = new FinalFantasyEndpoint();
        Timer finalFantasyEndpointTimer = new Timer("ffEndpointTimer", true);
        finalFantasyEndpointTimer.scheduleAtFixedRate(finalFantasyEndpoint.getTimerTask(), 0, finalFantasyEndpoint.refreshInterval());
        webServer.registerEndpoint("/games/ffxiv", finalFantasyEndpoint);

        webServer.start();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }

    public static String getEnvValue(String string) {
        return System.getenv(string) != null ? System.getenv(string) : env.get(string);
    }
}
