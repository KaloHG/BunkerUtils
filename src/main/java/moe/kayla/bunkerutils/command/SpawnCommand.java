package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {
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

        if(!player.hasPermission("bu.ctworld") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        if(!BunkerUtils.INSTANCE.getBunkerManager().inBunkerWorld(player.getWorld())) {
            player.sendMessage(ChatColor.RED + "You are not in a bunker world!");
            return true;
        }

        if(!args[0].equalsIgnoreCase("defenders") && !args[0].equalsIgnoreCase("attackers")) {
            player.sendMessage(ChatColor.RED + "Your arguments are invalid, optional args are Defenders or Attackers.");
            return false;
        }
        Bunker bunkie = BunkerUtils.INSTANCE.getBunkerManager().fetchBunkerByWorld(player.getWorld());
        //Any case other than defenders or attackers has been excluded
        if(args[0].equalsIgnoreCase("defenders")) {
            bunkie.setDefenderSpawn(player.getLocation());
            player.sendMessage(ChatColor.GOLD + "Set spawn on bunker " +
                    ChatColor.DARK_PURPLE + bunkie.getName() +
                    ChatColor.GOLD + " for " + ChatColor.GREEN + "Defenders");
        } else {
            bunkie.setAttackerSpawn(player.getLocation());
            player.sendMessage(ChatColor.GOLD + "Set spawn on bunker " +
                    ChatColor.DARK_PURPLE + bunkie.getName() +
                    ChatColor.GOLD + " for " + ChatColor.RED + "Attackers");
        }
        return true;
    }
}
