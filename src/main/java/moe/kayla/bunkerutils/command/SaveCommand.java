package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author Kayla
 * SaveCommand Class File
 *
 * @Command - /bctworld
 */
public class SaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Provide the arguments please.");
            return false;
        }
        if(!player.hasPermission("bu.ctworld") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        String name = args[0];
        String desc = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        Bunker newBunker = new Bunker(UUID.randomUUID(), name, player.getWorld().getName(), player.getName(), desc, null, null);
        if(BunkerUtils.INSTANCE.getBunkerDAO().createNewReinWorld(newBunker)) {
            player.sendMessage(ChatColor.GREEN + "Successfully created Bunker " + ChatColor.DARK_PURPLE + name + ChatColor.DARK_PURPLE + ".");
            BunkerUtils.INSTANCE.getLogger().info(ChatColor.GOLD + "Created new Bunker: " + ChatColor.GREEN + newBunker.getName());
        } else {
            player.sendMessage(ChatColor.RED + "Failed to save Bunker to database, contact an administrator.");
            BunkerUtils.INSTANCE.getLogger().severe("Failed to save a bunker due to DB Failure.");
        }
        return true;
    }
}
