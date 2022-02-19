package moe.kayla.bunkerutils.listener;

import com.devotedmc.ExilePearl.event.PlayerFreedEvent;
import com.devotedmc.ExilePearl.event.PlayerPearledEvent;
import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
            Arena arena = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(killer.getWorld());
            Bukkit.broadcastMessage(ChatColor.AQUA + event.getPearl().getPlayerName() + ChatColor.GOLD + " has been " + ChatColor.LIGHT_PURPLE
                    + "pearled " + ChatColor.GOLD + "by " + ChatColor.RED + event.getPearl().getKillerName() + ChatColor.GOLD + ".");
            arena.getPearled().addPlayer(event.getPearl().getPlayer());
            event.getPearl().getPlayer().setDisplayName(ChatColor.DARK_PURPLE + event.getPearl().getPlayer().getName());
            event.getPearl().getPlayer().setPlayerListName(ChatColor.DARK_PURPLE + event.getPearl().getPlayer().getName());
            event.getPearl().getPlayer().sendMessage(ChatColor.GREEN + "Spectator mode has now been enabled. If you join the arena your gamemode should automatically set.");
            event.getPearl().getPlayer().setGameMode(GameMode.SPECTATOR);
            BunkerUtils.INSTANCE.sendPlayerPearledMessage(event.getPearl());
        } else {
            //Is null, invalidate pearl wasn't made in an arena.
            event.setCancelled(true);
            killer.sendMessage(ChatColor.RED + "You cannot pearl players outside an arena.");
        }
    }

    /**
     * FreeEvent Handler
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
                    event.getPearl().getPlayer().setGameMode(GameMode.SURVIVAL);
                }
            }
        } else {
            //Handling in the future maybe?
        }
    }

    /**
     * Handling SpectatorEvents
     */
    @EventHandler
    public void spectatorTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            if(BunkerUtils.INSTANCE.getArenaManager().isPlayerPearled(event.getPlayer())) {
                if(event.getFrom().getWorld() != event.getTo().getWorld()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Pearled players cannot use spectator teleport to teleport out of arenas.");
                    event.setCancelled(true);
                }
            }
        }
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            if(BunkerUtils.INSTANCE.getArenaManager().isPlayerPearled(event.getPlayer())) {
                if(event.getTo().getWorld().getName().equals(BunkerUtils.INSTANCE.getArenaManager().getPlayerArena(event.getPlayer()).getWorld())) {
                    //We schedule a bit later so the teleport can succeed...
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Because you are " + ChatColor.AQUA + "pearled " + ChatColor.GREEN + "you were automatically switched into spectator.");
                    Bukkit.getScheduler().runTaskLater(BunkerUtils.INSTANCE, () -> { event.getPlayer().setGameMode(GameMode.SPECTATOR); }, 20L);
                }
            }
        }
    }
}
