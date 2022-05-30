package moe.kayla.bunkerutils.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.citadel.reinforcementtypes.ReinforcementType;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.group.Group;

/**
 * @Author Kayla
 * AreaSpecificCommand Class File
 *
 * @Command - /bctars <group> <block>
 */
public class AreaSpecificCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Provide arguments.");
            return false;
        }

        if(BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getByItemStack(player.getItemInHand(), player.getWorld().getName()) == null) {
            player.sendMessage(ChatColor.RED + "You need to be holding a valid reinforcement item.");
            return true;
        }
        ReinforcementType reinType = BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getByItemStack(player.getItemInHand(), player.getWorld().getName());
        Group group = GroupManager.getGroup(args[0]);
        Material material = Material.valueOf(args[1]);
        if(group == null || material == null) {
            player.sendMessage(ChatColor.RED + "Material: " + material + " group: " + group + " one was invalid.");
            return true;
        }

        if(!player.hasPermission("bu.ctools") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        try {
            Region reg = BunkerUtils.INSTANCE.getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection();
            int i = 0;
            for(BlockVector3 xyz : reg) {
                Location loc = new Location(BukkitAdapter.adapt(reg.getWorld()), xyz.getX(), xyz.getY(), xyz.getZ());
                if(loc.getBlock().getType() == material) {
                    Reinforcement newRein = new Reinforcement(loc, reinType, group);
                    BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().putReinforcement(newRein);
                    i++;
                }
            }
            player.sendMessage(ChatColor.GREEN + "Successfully reinforced " + ChatColor.AQUA + i + ChatColor.GREEN + " blocks.");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Your WorldEdit selection is incomplete!");
            e.printStackTrace();
            return true;
        }

        return true;
    }
}
