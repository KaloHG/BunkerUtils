package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoteRestartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!BunkerUtils.INSTANCE.getBunkerConfiguration().getVoteRestart()) {
            sender.sendMessage(ChatColor.RED + "This command is disabled.");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(BunkerUtils.INSTANCE.addUserVote(player)) {
            player.sendMessage(ChatColor.GREEN + "Your vote was successfully registered! It will expire in 5 minutes.");
            Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + ChatColor.GOLD + " has voted for a restart.");
            BunkerUtils.INSTANCE.checkIfEnoughVotes();
        } else {
            player.sendMessage(ChatColor.RED + "You've already voted!");
        }
        return true;
    }
}
