package moe.kayla.bunkerutils.listener;

import com.devotedmc.ExilePearl.event.PlayerPearledEvent;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

//We need to cover for player deaths, player teleportation, player team join, etc.
public class TeamListener implements Listener {


    /**
     * PearlEvent Handler
     *
     * It's safe to assume that the killer is online and has not changed worlds, so we can just catch the world from them
     * @param event - pearl event
     */
    @EventHandler
    public void pearlEvent(PlayerPearledEvent event) {
        Player killer = Bukkit.getPlayer(event.getPearl().getKillerId());
        if(BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(killer.getWorld()) != null) {
            //Not null, start pearl handling.
        } else {
            //Is null, invalidate pearl wasn't made in an arena.
            event.setCancelled(true);
            killer.sendMessage(ChatColor.RED + "You cannot pearl players outside an arena.");
        }
    }
}
