package discord;

import endpoints.OnlineHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.TimerTask;

public class RefreshOnlineStatusTask extends TimerTask {

    private final DiscordBot discordBot;
    private final String userTag;

    protected RefreshOnlineStatusTask(String userTag, DiscordBot discordBot) {
        this.userTag = userTag;
        this.discordBot = discordBot;
    }

    @Override
    public void run() {
        JDA jda = discordBot.getJda();
        for (Guild guild : jda.getGuilds()) {
            Member member = guild.getMemberByTag(userTag);
            if (member != null) {
                OnlineHandler.discordStatus = member.getOnlineStatus();
                return;
            }
        }
    }
}
