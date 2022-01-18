package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @Author Kayla
 * ActiveCommand Class File
 *
 * @Command - /bactive
 */
public class ActiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "Provide arguments.");
            return false;
        }

        if(!player.hasPermission("bu.active") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        Bunker searchedBunker = BunkerUtils.INSTANCE.getBunkerManager().fetchBunkerByName(args[0]);
        if(searchedBunker == null) {
            player.sendMessage(ChatColor.RED + "Bunker " + ChatColor.DARK_PURPLE + args[0] + ChatColor.RED + " does not exist.");
            return true;
        }

        try {
            World world = Bukkit.getWorld(BunkerUtils.INSTANCE.getBunkerDAO().startReinWorld(searchedBunker, player));
            Location location;
            if(searchedBunker.getAttackerSpawn() == null) {
                location = new Location(world, 0, 64, 0);
            }
            location = searchedBunker.getAttackerSpawn();
            //Temporary until spawn-points.
            player.teleport(location);
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(ChatColor.GOLD + "BunkerWorld for Bunker: " + ChatColor.DARK_PURPLE + searchedBunker.getName()
            + ChatColor.GOLD + " created by " + ChatColor.DARK_PURPLE + searchedBunker.getAuthor() + ChatColor.GOLD
            + " was successfully loaded.");
            player.sendTitle(ChatColor.GOLD + "Entered " + ChatColor.DARK_PURPLE + searchedBunker.getName(),
                    ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + searchedBunker.getAuthor());
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
        } catch(Exception e) {
            player.sendMessage(ChatColor.RED + "Failed to create BunkerWorld for Bunker: " + ChatColor.DARK_PURPLE + searchedBunker.getName());
        }
        return true;
    }
}
