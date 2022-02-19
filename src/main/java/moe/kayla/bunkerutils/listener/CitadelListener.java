package moe.kayla.bunkerutils.listener;

import isaac.bastion.event.BastionDestroyedEvent;
import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import vg.civcraft.mc.citadel.events.ReinforcementDamageEvent;

public class CitadelListener implements Listener {

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

    @EventHandler
    public void onBastionBreak(BastionDestroyedEvent event) {
        Arena a = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(event.getBastion().getLocation().getWorld());
        if(a != null) {
            //In an arena, make announcement.
            BunkerUtils.INSTANCE.sendBastionBreakMessage(a, event);
        }
    }
}
