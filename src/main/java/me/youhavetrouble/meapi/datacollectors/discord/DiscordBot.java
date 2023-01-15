package me.youhavetrouble.meapi.datacollectors.discord;

import me.youhavetrouble.meapi.MeAPI;
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
    private final String discordUserTag;


    public DiscordBot(String token) {
        this.token = token;
        this.discordUserTag = MeAPI.getEnvValue("DISCORD_USER_TAG");
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

    public OnlineStatus getOnlineStatus() {
        if (discordUserTag == null) return OnlineStatus.OFFLINE;
        for (Guild guild : jda.getGuilds()) {
            Member member = guild.getMemberByTag(discordUserTag);
            if (member != null) {
                return member.getOnlineStatus();
            }
        }
        return OnlineStatus.OFFLINE;
    }

    public JDA getJda() {
        return jda;
    }
}
