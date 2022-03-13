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
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

public class AreaReinRemoveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("bu.ctools") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        try {
            Region reg = BunkerUtils.INSTANCE.getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection();
            int i = 0;
            for (BlockVector3 xyz : reg) {
                Location loc = new Location(BukkitAdapter.adapt(reg.getWorld()), xyz.getX(), xyz.getY(), xyz.getZ());
                if (BunkerUtils.INSTANCE.getCitadel().getConfigManager().getBlacklistedMaterials().contains(loc.getBlock().getType())) {
                    continue;
                }
                if (loc.getBlock().getType().isAir()) {
                    continue;
                }
                if (BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().getReinforcement(loc) != null) {
                    BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().getReinforcement(loc).setHealth(0);
                    i++;
                }
            }
            player.sendMessage(ChatColor.GREEN + "Successfully removed reinforcements on " + ChatColor.AQUA + i + ChatColor.GREEN + " blocks.");
            if (i > 10000) {
                player.sendMessage(ChatColor.ITALIC.toString() + ChatColor.YELLOW + "De-reinforcing more than 10,000 blocks at once may cause a server crash when" +
                        "the BunkerMap is saved without a restart first. Use caution when reinforcing at amounts higher than 30K-50K.");
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Your WorldEdit selection is incomplete!");
            e.printStackTrace();
            return true;
        }

        return true;
    }
}
