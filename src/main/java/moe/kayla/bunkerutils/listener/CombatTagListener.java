package moe.kayla.bunkerutils.listener;

import moe.kayla.bunkerutils.BunkerUtils;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CombatTagListener implements Listener {

    @EventHandler
    public void onCombatTag(PlayerCombatTagEvent event) {
        //We check if player is in arena and modify the tag time if they are.
        if(!BunkerUtils.INSTANCE.getArenaManager().isPlayerInArena(event.getPlayer())) {
            event.setTagDuration(BunkerUtils.INSTANCE.getBunkerConfiguration().getArenaTagTime());
        }
    }
}
