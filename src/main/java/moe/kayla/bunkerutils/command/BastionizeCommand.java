package moe.kayla.bunkerutils.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import isaac.bastion.Bastion;
import isaac.bastion.BastionType;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.citadel.reinforcementtypes.ReinforcementType;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.group.Group;

public class BastionizeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 3) {
            player.sendMessage(ChatColor.RED + "Provide arguments.");
            return false;
        }

        if(BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getByItemStack((player.getItemInHand())) == null) {
            player.sendMessage(ChatColor.RED + "You need to be holding a valid reinforcement item.");
            return true;
        }
        ReinforcementType reinType = BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getByItemStack(player.getInventory().getItemInMainHand());
        Group group = GroupManager.getGroup(args[0]);
        Material mat = Material.valueOf(args[1]);
        BastionType bt = BastionType.getBastionType(args[2]);
        if(mat == null || group == null || reinType == null) {
            player.sendMessage(ChatColor.RED + "BlockType: " + mat + " Group: " + group + " ReinType: " + reinType + " BastionType: " + bt + " failed to function.");
            return true;
        }
        if(!player.hasPermission("bu.ctools") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        try {
            Region r = BunkerUtils.INSTANCE.getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection();
            int i = 0;
            for(BlockVector3 xyz : r) {
                Block block = new Location(BukkitAdapter.adapt(r.getWorld()), xyz.getX(), xyz.getY(), xyz.getZ()).getBlock();
                if(block.getType() == mat) {
                    block.setType(bt.getMaterial());
                    Reinforcement rein = new Reinforcement(block.getLocation(), reinType, group);
                    BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().putReinforcement(rein);
                    Bastion.getBastionStorage().createBastion(block.getLocation(), bt, player);
                    i++;
                }
            }
            player.sendMessage(ChatColor.GREEN + "Created " + ChatColor.AQUA + i + ChatColor.GREEN + " bastions successfully.");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "There is no selection available. (//wand?)");
            return true;
        }
        return true;
    }
}
