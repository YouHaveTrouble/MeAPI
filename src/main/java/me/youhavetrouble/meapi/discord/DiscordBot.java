package me.youhavetrouble.meapi.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class DiscordBot extends Thread {

    private final String token;

    private JDA jda;

    public DiscordBot(String token) {
        this.token = token;
    }

    @Override
    public void run() {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.CLIENT_STATUS);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
            jda = builder.build();
            jda.setAutoReconnect(true);
        } catch (LoginException e) {
            System.out.println("Discord bot failed to log in");
        }
        super.run();
    }

    public OnlineStatus getOnlineStatus(String userTag) {
        for (Guild guild : jda.getGuilds()) {
            Member member = guild.getMemberByTag(userTag);
            if (member != null) {
                return member.getOnlineStatus();
            }
        }
        return null;
    }

    public JDA getJda() {
        return jda;
    }
}
