package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
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
            + "\n" + ChatColor.GOLD + "/arena join <arena> [Defenders|Attackers]"
            + "\n" + ChatColor.GOLD + "/arena list"
            + "\n" + ChatColor.GOLD + "/arena close <arena>");
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

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("close")) {
                if(!player.hasPermission("bu.ctworld") && !player.isOp()) {
                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
                }
            }
        }

        if(args.length > 2) {
            if(args[0].equalsIgnoreCase("join")) {
                Arena arena = BunkerUtils.INSTANCE.getArenaManager().getArenaByHost(args[1]);
                if(arena == null) {
                    player.sendMessage(ChatColor.DARK_PURPLE + args[1] + ChatColor.RED +" is not an active arena.");
                    return true;
                }
                switch(args[2].toLowerCase()) {
                    case "defenders":
                        if(arena.getBunker().getDefenderSpawn() == null) {
                            player.sendMessage(ChatColor.RED + "The defender spawn for this arena is not set! Contact an administrator.");
                            return true;
                        }
                        //Remove Defender from Attacker Group.
                        GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getAttackerGroup()).removeMember(player.getUniqueId());
                        GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getDefenderGroup()).addMember(player.getUniqueId(), GroupManager.PlayerType.MODS);
                        Location defenderSpawn = arena.getBunker().getDefenderSpawn();
                        defenderSpawn.setWorld(Bukkit.getWorld(arena.getWorld()));
                        player.teleport(defenderSpawn);
                        player.sendTitle(ChatColor.GOLD + "Joined " + ChatColor.DARK_PURPLE + arena.getBunker().getName(),
                                ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + arena.getBunker().getAuthor());
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                        return true;
                    case "attackers":
                        if(arena.getBunker().getAttackerSpawn() == null) {
                            player.sendMessage(ChatColor.RED + "The attacker spawn for this arena is not set! Contact an administrator.");
                            return true;
                        }
                        //Remove Attacker from Defender Group.
                        GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getDefenderGroup()).removeMember(player.getUniqueId());
                        GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getAttackerGroup()).addMember(player.getUniqueId(), GroupManager.PlayerType.MODS);
                        Location attackerSpawn = arena.getBunker().getAttackerSpawn();
                        player.sendTitle(ChatColor.GOLD + "Joined " + ChatColor.DARK_PURPLE + arena.getBunker().getName(),
                                ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + arena.getBunker().getAuthor());
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                        player.teleport(attackerSpawn);
                        return true;
                }
            }
        }
        return true;
    }
}
