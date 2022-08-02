package discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.Timer;

public class DiscordBot extends Thread {

    private final String token;
    private final String userId;

    private JDA jda;

    public DiscordBot(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    @Override
    public void run() {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.CLIENT_STATUS);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
            jda = builder.build();
            jda.setAutoReconnect(true);
            if (userId != null) updateOnlineData();
        } catch (LoginException e) {
            System.out.println("Discord bot failed to log in");
        }
        super.run();
    }

    private void updateOnlineData() {
        Timer timer = new Timer("OnlineStatusTimer", true);
        timer.scheduleAtFixedRate(new RefreshOnlineStatusTask(userId, this), 1000, 1000*30);
    }

    public JDA getJda() {
        return jda;
    }
}
