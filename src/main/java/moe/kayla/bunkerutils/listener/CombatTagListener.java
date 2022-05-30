package moe.kayla.bunkerutils.listener;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.config.ConfigurationService;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @Author Kayla
 * CombatTagListener Class File
 */
public class CombatTagListener implements Listener {

    /**
     * Modifies Out of arena tag time so duels and shit still work.
     * @param event - PlayerCombatTagEvent, from CombatTagPlus.
     */
    @EventHandler
    public void onCombatTag(PlayerCombatTagEvent event) {
        //We check if player is in arena and modify the tag time if they are.
        if(!BunkerUtils.INSTANCE.getArenaManager().isPlayerInArena(event.getPlayer())) {
            event.setTagDuration(ConfigurationService.ARENATAGTIME);
        }
    }
}
