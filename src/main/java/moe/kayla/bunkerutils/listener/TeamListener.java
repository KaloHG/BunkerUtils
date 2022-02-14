package moe.kayla.bunkerutils.listener;

import com.devotedmc.ExilePearl.event.PlayerFreedEvent;
import com.devotedmc.ExilePearl.event.PlayerPearledEvent;
import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

//We need to cover for player deaths, player teleportation, player team join, etc.

/**
 * @Author Kayla
 * TeamListener Class File
 */
public class TeamListener implements Listener {


    /**
     * PearlEvent Handler
     *
     * It's safe to assume that the killer is online and has not changed worlds, so we can just catch the world from them
     * @param event - PlayerPearledEvent, from ExilePearl.
     */
    @EventHandler
    public void pearlEvent(PlayerPearledEvent event) {
        Player killer = Bukkit.getPlayer(event.getPearl().getKillerId());
        if(BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(killer.getWorld()) != null) {
            //Not null, start pearl handling.
            Arena arena = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(killer.getWorld());
            Bukkit.broadcastMessage(ChatColor.AQUA + event.getPearl().getPlayerName() + ChatColor.GOLD + " has been " + ChatColor.LIGHT_PURPLE
                    + "pearled " + ChatColor.GOLD + "by " + ChatColor.RED + event.getPearl().getKillerName() + ChatColor.GOLD + ".");
            arena.getPearled().addPlayer(event.getPearl().getPlayer());
            event.getPearl().getPlayer().setDisplayName(ChatColor.DARK_PURPLE + event.getPearl().getPlayer().getName());
            event.getPearl().getPlayer().setPlayerListName(ChatColor.DARK_PURPLE + event.getPearl().getPlayer().getName());
        } else {
            //Is null, invalidate pearl wasn't made in an arena.
            event.setCancelled(true);
            killer.sendMessage(ChatColor.RED + "You cannot pearl players outside an arena.");
        }
    }

    /**
     * Free Event Handler
     * @param event - PlayerFreedEvent, from ExilePearl.
     */
    @EventHandler
    public void pearlFree(PlayerFreedEvent event) {
        if(BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(event.getPearl().getLocation().getWorld()) != null) {
            //Pearl was freed in an arena world, lets check to see if the player is on the pearled team
            Arena a = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(event.getPearl().getLocation().getWorld());
            if(a.getPearled().getPlayers().contains(event.getPearl().getPlayerId())) {
                BunkerUtils.INSTANCE.getLogger().info("Pearled player freed, stripping pearl rank for arena.");
                a.getPearled().removePlayer(event.getPearl().getPlayer());
                Bukkit.broadcastMessage(ChatColor.AQUA + event.getPearl().getPlayerName() + ChatColor.GOLD + " has been " + ChatColor.GREEN + "freed"
                + ChatColor.GOLD + ".");
                if(a.isPlayerInTeam(event.getPearl().getPlayer())) {
                    switch(a.getPlayerTeamType(event.getPearl().getPlayer())) {
                        case ATTACKERS:
                            event.getPearl().getPlayer().setDisplayName(ChatColor.DARK_RED + event.getPearl().getPlayer().getName());
                            event.getPearl().getPlayer().setPlayerListName(ChatColor.DARK_RED + event.getPearl().getPlayer().getName());
                        case DEFENDERS:
                            event.getPearl().getPlayer().setDisplayName(ChatColor.DARK_GREEN + event.getPearl().getPlayer().getName());
                            event.getPearl().getPlayer().setPlayerListName(ChatColor.DARK_GREEN + event.getPearl().getPlayer().getName());
                        case INVALID:
                            event.getPearl().getPlayer().setDisplayName(event.getPearl().getPlayer().getName());
                            event.getPearl().getPlayer().setPlayerListName(event.getPearl().getPlayer().getName());
                    }
                } else {
                    //Arena likely ended, just clear name.
                    event.getPearl().getPlayer().setDisplayName(event.getPearl().getPlayer().getName());
                    event.getPearl().getPlayer().setPlayerListName(event.getPearl().getPlayer().getName());
                }
            }
        } else {
            //Handling in the future maybe?
        }
    }
}
