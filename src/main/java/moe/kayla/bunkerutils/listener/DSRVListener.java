package moe.kayla.bunkerutils.listener;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import moe.kayla.bunkerutils.model.discord.EmbedInitializer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

/**
 * @Author Kayla
 * DSRVListener Class File
 */
public class DSRVListener {

    /*
     * DiscordSRV has weird proprietary subscribers. So we have to use their own in-house event handling.
     *
     * This class is intended to just create informational messages for discord and stuff.
     */
    @Subscribe
    public void onVerifyEvent(AccountLinkedEvent event) {
        //We can read the Discord and Minecraft (Offline) User's info from here.
        EmbedBuilder eb = EmbedInitializer.getVerifEmbed((User) event.getUser(), event.getPlayer());
        DiscordSRV.getPlugin().getConsoleChannel().sendMessage((Message) eb.build()).queue();
    }

    @Subscribe
    public void onUnverifEvent(AccountUnlinkedEvent event) {
        DiscordSRV.getPlugin().getConsoleChannel().sendMessage("**" + event.getPlayer().getName() + "** has *deverified* themselves! (Former D/ID: `" + event.getDiscordId() + "`)").queue();
    }
}
