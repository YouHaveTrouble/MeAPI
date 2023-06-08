package me.youhavetrouble.meapi.datacollectors.discord;

import me.youhavetrouble.meapi.MeAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot extends Thread {

    private final String token;

    private JDA jda;
    private final String discordUserId;

    public DiscordBot(String token) {
        this.token = token;
        this.discordUserId = MeAPI.getEnvValue("DISCORD_USER_ID");
    }

    @Override
    public void run() {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.CLIENT_STATUS);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
        jda = builder.build();
        jda.setAutoReconnect(true);

        super.run();
    }

    public OnlineStatus getOnlineStatus() {
        if (discordUserId == null) return OnlineStatus.OFFLINE;
        for (Guild guild : jda.getGuilds()) {
            Member member = guild.getMemberById(discordUserId);
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
