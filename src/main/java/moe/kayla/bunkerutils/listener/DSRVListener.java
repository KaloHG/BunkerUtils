package moe.kayla.bunkerutils.listener;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.AchievementMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.AchievementMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.discord.EmbedInitializer;

/**
 * Absolutely cursed casting for Discord Handling.
 */
public class DSRVListener {

    /**
     * DiscordSRV has weird proprietary subscribers. So we have to use their own in-house event handling.
     * @param event - Event to be passed.
     */
    @Subscribe
    public void onVerifyEvent(AccountLinkedEvent event) {
        //We can read the Discord and Minecraft (Offline) User's info from here.
        EmbedBuilder eb = EmbedInitializer.getVerifEmbed( event.getUser(), event.getPlayer());
        DiscordSRV.getPlugin().getConsoleChannel().sendMessage(eb.build()).queue();
    }

    @Subscribe
    public void onUnverifEvent(AccountUnlinkedEvent event) {
        DiscordSRV.getPlugin().getConsoleChannel().sendMessage("**" + event.getPlayer().getName() + "** has *deverified* themselves! (Former D/ID: `" + event.getDiscordId() + "`)").queue();
    }

    @Subscribe
    public void onAdvancementEvent(AchievementMessagePreProcessEvent event) {
        if(BunkerUtils.INSTANCE.getBunkerConfiguration().getAdvancementsDisabled()) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onAdvancementPostEvent(AchievementMessagePostProcessEvent event) {
        if(BunkerUtils.INSTANCE.getBunkerConfiguration().getAdvancementsDisabled()) {
            event.setCancelled(true);
        }
    }
}
