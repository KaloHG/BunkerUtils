package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

        /**
         * Usage Display
         */
        if(args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "-=<" + ChatColor.DARK_PURPLE + "BunkerUtils" + ChatColor.GOLD + "=-"
            + "\n" + ChatColor.GOLD + "/arena join"
            + "\n" + ChatColor.GOLD + "/arena list"
            + "\n" + ChatColor.GOLD + "/arena create"
            + "\n" + ChatColor.RED + "/arena close <arena> (ADMIN ONLY)");
            return true;
        }
        /**
         * Lists active arenas
         */
        if(args[0].equalsIgnoreCase("list")) {
            String arenaList = "";
            for(Arena arena : BunkerUtils.INSTANCE.getArenaManager().getArenas()) {
                arenaList = arenaList + arena.getHost() + ", ";
            }
            player.sendMessage(ChatColor.GOLD + "Open Arenas: " + arenaList + "join any with /arena join.");
            return true;
        }

        /**
         * Create argument
         */
        if(args[0].equals("create")) {
            //perm check
            if(!player.hasPermission("bu.start") && !player.isOp()) {
                player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
                return true;
            }
            //checks if there is more than one arena active
            if(BunkerUtils.INSTANCE.getArenaManager().activeArenaWorlds().size() > 0){
                player.sendMessage(ChatColor.RED + "There is already an arena open. /arena join!");
                return true;
            }
            //checks if there is an arena in the creation process
            if(BunkerUtils.INSTANCE.bunkerDAO.isArenaLoading){
                player.sendMessage(ChatColor.GOLD+ "There is already an arena loading...");
                return true;
            }
            //checks if player already has an arena open
            if(BunkerUtils.INSTANCE.getArenaManager().getArenaByHost(player.getName()) != null) {
                player.sendMessage(ChatColor.DARK_RED + "You already have an arena open.");
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Join your arena and type /arena close if you would like to close it.");
                return true;
            }
            BunkerUtils.INSTANCE.getCreateGui().openCreateGui(player);
            return true;
        }

        /**
         * Join argument
         */
        if(args[0].equalsIgnoreCase("join")) {
            BunkerUtils.INSTANCE.getJoinGui().openJoinGui(player);
            return true;
        }
        /**
         * Close argument
         */
        if(args[0].equalsIgnoreCase("close")) {
            if(!player.hasPermission("bu.ctworld") && !player.isOp()) {
                if(BunkerUtils.INSTANCE.getArenaManager().isPlayerInArena(player)) {
                    Arena a = BunkerUtils.INSTANCE.getArenaManager().getArenaByWorld(player.getWorld());
                    if(a.getHost().equals(player.getName())) {
                        player.sendMessage(ChatColor.GREEN + "Identified current arena as your own. Starting closure.");
                        if(a.close()) {
                            player.sendMessage(ChatColor.GREEN + "Arena was closed successfully.");
                        } else {
                            player.sendMessage(ChatColor.RED + "Arena closure failed! Contact an admin. (Wait... you are an admin! psst, contact a dev with the stack trace.)");
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "You are not the owner of this arena world.");
                    }
                    return true;
                }
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
