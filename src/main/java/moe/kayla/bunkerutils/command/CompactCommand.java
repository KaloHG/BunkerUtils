package moe.kayla.bunkerutils.command;

import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.civmodcore.CivModCorePlugin;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;

/**
 * @Author Kayla
 * CompactCommand Class File
 *
 * @Command - /compact
 */
public class CompactCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE No.");
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("bu.ctools") && !player.isOp()) {
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }

        if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "You need to be holding an item in your main hand to run this command.");
            return true;
        }

        ItemStack is = player.getInventory().getItemInMainHand();
        //apply lore
        ItemUtils.addLore(is, BunkerUtils.INSTANCE.getBunkerConfiguration().getCompactLore());
        is.setAmount(is.getMaxStackSize());
        player.sendMessage(ChatColor.GREEN + "Successfully compacted " + ChatColor.AQUA + is.getType().name());
        return true;
    }
}
