package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @Author Kayla
 * BunkerListCommand Class File
 *
 * @Command - /blist
 */
public class BunkerListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("bu.ctworld") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "-=<" + ChatColor.DARK_PURPLE + "Bunkers" + ChatColor.GOLD + ">=-");
        for(Bunker b : BunkerUtils.INSTANCE.getBunkerManager().getBunkers()) {
            player.sendMessage(ChatColor.GOLD + b.getName() + ChatColor.GRAY + " Author: " + ChatColor.AQUA + b.getAuthor() +
                    ChatColor.GRAY + " Description: " + ChatColor.AQUA + b.getDescription());
        }
        return true;
    }
}
