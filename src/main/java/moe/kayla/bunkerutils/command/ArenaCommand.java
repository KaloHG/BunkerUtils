package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;

/**
 * @Author Kayla
 * ArenaCommand Class File
 *
 * @Command - /arena
 */
public class ArenaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "-=<" + ChatColor.DARK_PURPLE + "BunkerUtils" + ChatColor.GOLD + "=-"
            + "\n" + ChatColor.GOLD + "/arena join"
            + "\n" + ChatColor.GOLD + "/arena list"
            + "\n" + ChatColor.GOLD + "/arena create"
            + "\n" + ChatColor.RED + "/arena close <arena> (ADMIN ONLY)");
            return true;
        }

        if(args[0].equalsIgnoreCase("list")) {
            String arenaList = "";
            for(Arena arena : BunkerUtils.INSTANCE.getArenaManager().getArenas()) {
                arenaList = arenaList + arena.getHost() + ", ";
            }
            player.sendMessage(ChatColor.GOLD + "Open Arenas: " + arenaList + "join any with /arena join.");
            return true;
        }

        if(args[0].equals("create")) {
            if(!player.hasPermission("bu.start") && !player.isOp()) {
                player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
                return true;
            }
            BunkerUtils.INSTANCE.getCreateGui().openCreateGui(player);
            return true;
        }
        if(args[0].equalsIgnoreCase("join")) {
            BunkerUtils.INSTANCE.getJoinGui().openJoinGui(player);
            return true;
        }
        if(args[0].equalsIgnoreCase("close")) {
            if(!player.hasPermission("bu.ctworld") && !player.isOp()) {
                player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
                return true;
            }
            Arena a;
            //User provided arguments.
            if(args.length > 1) {
                a = BunkerUtils.INSTANCE.getArenaManager().getArenaByHost(args[1]);
                if(a == null) {
                    player.sendMessage(ChatColor.RED + "The host provided does not have an open arena.");
                    return true;
                }
            } else {
                a = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(player.getWorld());
                if(a == null) {
                    player.sendMessage(ChatColor.RED + "The world you are currently in is not an arena. Try specifying a host?");
                    return true;
                }
            }
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Starting Arena Closure for " + a.getHost() + "'s Arena.");
            if(a.close()) {
                player.sendMessage(ChatColor.GREEN + "Arena was closed successfully.");
            } else {
                player.sendMessage(ChatColor.RED + "Arena closure failed! Contact an admin. (Wait... you are an admin! psst, contact a dev with the stack trace.)");
            }
            return true;
        }

        if(args.length > 2) {
        }
        return true;
    }
}
