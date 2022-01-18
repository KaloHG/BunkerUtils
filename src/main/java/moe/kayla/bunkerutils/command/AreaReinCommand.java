package moe.kayla.bunkerutils.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.citadel.reinforcementtypes.ReinforcementType;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

public class AreaReinCommand implements CommandExecutor {
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

        if(BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getByItemStack((player.getItemInHand())) == null) {
            player.sendMessage(ChatColor.RED + "You need to be holding a valid reinforcement item.");
            return true;
        }
        ReinforcementType reinType = BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getByItemStack(player.getInventory().getItemInMainHand());
        Group group = GroupManager.getGroup(args[0]);

        if(!player.hasPermission("bu.ctools") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        try {
            Region reg = BunkerUtils.INSTANCE.getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection();
            for(BlockVector3 xyz : reg) {
                Location loc = new Location(BukkitAdapter.adapt(reg.getWorld()), xyz.getX(), xyz.getY(), xyz.getZ());
                Reinforcement newRein = new Reinforcement(loc, reinType, group);
                BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().putReinforcement(newRein);
            }
            player.sendMessage(ChatColor.GREEN + "Successfully reinforced " + ChatColor.AQUA + reg.getArea() + ChatColor.GREEN + " blocks.");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Your WorldEdit selection is incomplete!");
            e.printStackTrace();
            return true;
        }

        return true;
    }
}
