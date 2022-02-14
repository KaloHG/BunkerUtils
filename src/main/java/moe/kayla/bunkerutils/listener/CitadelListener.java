package moe.kayla.bunkerutils.listener;

import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import vg.civcraft.mc.citadel.events.ReinforcementDamageEvent;

/**
 * @Author Kayla
 * CitadelListener Class File
 */
public class CitadelListener implements Listener {

    /**
     * Arena Damage Modifier Listener. Used to modify damage based on arena multiplier.
     * @param event - ReinforcementDamageEvent, from Citadel.
     */
    @EventHandler
    public void onReinforcementDamage(ReinforcementDamageEvent event) {
        Player player = event.getPlayer();
        if(BunkerUtils.INSTANCE.getArenaManager().isPlayerInArena(player)) {
            int mult = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(player.getWorld()).getCtDebuff();
            //Now apply multiplier to rein dmg.
            float modify = event.getDamageDone() * mult;
            event.setDamageDone(modify);
        }
    }
}
