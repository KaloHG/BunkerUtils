package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameLayerPlugin;

public class ArenaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 1) {
            player.sendMessage(ChatColor.GOLD + "-=<" + ChatColor.DARK_PURPLE + "BunkerUtils" + ChatColor.GOLD + "=-"
            + "\n" + ChatColor.GOLD + "/arena join <arena> [Defenders|Attackers]"
            + "\n" + ChatColor.GOLD + "/arena list");
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                String arenaList = "";
                for(Arena arena : BunkerUtils.INSTANCE.getArenaManager().getArenas()) {
                    arenaList = arenaList + arena.getHost() + ", ";
                }
                player.sendMessage(ChatColor.GOLD + "Open Arenas: " + arenaList + "join any with /arena join.");
                return true;
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
                        //Remove Defender from Attacker Group.
                        NameLayerPlugin.getGroupManagerDao().removeMember(player.getUniqueId(), BunkerUtils.INSTANCE.getBunkerConfiguration().getAttackerGroup());
                        NameLayerPlugin.getGroupManagerDao().addMember(player.getUniqueId(), BunkerUtils.INSTANCE.getBunkerConfiguration().getDefenderGroup(), GroupManager.PlayerType.MODS);
                        Location defenderSpawn = arena.getBunker().getDefenderSpawn();
                        defenderSpawn.setWorld(Bukkit.getWorld(arena.getWorld()));
                        player.teleport(defenderSpawn);
                    case "attackers":

                    default:
                        return false;
                }
            }
        }
        return true;
    }
}
