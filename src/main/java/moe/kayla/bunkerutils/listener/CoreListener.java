package moe.kayla.bunkerutils.listener;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * @Author Kayla
 * CoreListener Class File
 */
public class CoreListener implements Listener {

    /**
     * Block Break Event Handler, used to close an arena when the core block of said arena is broken.
     * @param event - BlockBreakEvent, normal Bukkit event.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(BunkerUtils.INSTANCE.getArenaManager().isPlayerInArena(event.getPlayer())) {
            Arena current = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(event.getPlayer().getWorld());
            World world = Bukkit.getWorld(current.getWorld());
            if(event.getBlock().getLocation() == current.getAttackerBeacon()) {
                for(Player player : world.getPlayers()) {
                    player.sendTitle(ChatColor.DARK_GREEN + "Defenders " + ChatColor.GOLD + "Win", ChatColor.GRAY + "The arena will close in 10 minutes");
                    current.getPearled().sendTeamMessage(ChatColor.GOLD + "Pearled players are " + ChatColor.GREEN + "freed " + ChatColor.GOLD + "on arena closure.");
                    //arena closure functionality not done yet. TODO
                    world.createExplosion(current.getAttackerBeacon(), 4F);
                }
                Bukkit.getScheduler().runTaskLater(BunkerUtils.INSTANCE, current::close, 600000);
            } else if(event.getBlock().getLocation() == current.getDefenderBeacon()) {
                for(Player player : world.getPlayers()) {
                    player.sendTitle(ChatColor.RED + "Attackers " + ChatColor.GOLD + "Win", ChatColor.GRAY + "The arena will close in 10 minutes");
                    current.getPearled().sendTeamMessage(ChatColor.GOLD + "Pearled players are " + ChatColor.GREEN + "freed " + ChatColor.GOLD + "on arena closure.");
                    //arena closure functionality not done yet. TODO
                    world.createExplosion(current.getDefenderBeacon(), 4F);
                }
                Bukkit.getScheduler().runTaskLater(BunkerUtils.INSTANCE, current::close, 600000);
            }
        }
    }
}
