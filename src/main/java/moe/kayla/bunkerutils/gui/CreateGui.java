package moe.kayla.bunkerutils.gui;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;

public class CreateGui {
    private static final int rowLength = 9;

    public void openCreateGui(Player player) {
        ClickableInventory createGui = new ClickableInventory(9, "Bunker Selection");

        for(Bunker b : BunkerUtils.INSTANCE.getBunkerManager().getBunkers()) {
            ItemStack is = new ItemStack(Material.GRASS_BLOCK);
            is.setAmount(1);
            ItemUtils.setDisplayName(is, ChatColor.GOLD + b.getName());
            ItemUtils.addLore(is, ChatColor.DARK_GRAY + "Author: " + ChatColor.DARK_PURPLE + b.getAuthor(),
                    ChatColor.DARK_GRAY + "Description: " + ChatColor.DARK_PURPLE + b.getDescription(),
                    ChatColor.DARK_GRAY + b.getUuid().toString());
            Clickable clickBunker = new Clickable(is) {
                @Override
                protected void clicked(Player player) {
                    openScalingGui(player, b);
                }
            };
            createGui.addSlot(clickBunker);
        }
        createGui.showInventory(player);
    }

    public void openScalingGui(Player p, Bunker b) {
        ClickableInventory scaleGui = new ClickableInventory(9, "Citadel Scaling");

        for(int i = 1; i < 10; i++) {
            ItemStack is = new ItemStack(Material.GOLD_BLOCK);
            is.setAmount(1);
            ItemUtils.setDisplayName(is, ChatColor.GOLD + "x" + i);
            ItemUtils.setLore(is, ChatColor.GRAY + "This number will be multiplied by the damage",
                    ChatColor.GRAY + "applied to a reinforcement in order to scale the reinforcement.",
                    ChatColor.GRAY + "E.x. Each Reinforcement break is 1 damage normally",
                    ChatColor.GRAY + "but now it is 1*"+ i + " which makes it " + i  + " damage.",
                    ChatColor.DARK_GRAY + "This value also applies to bastion damage.");
            int finalI = i;
            Clickable scaleClick = new Clickable(is) {
                @Override
                protected void clicked(Player player) {
                    String world = BunkerUtils.INSTANCE.getBunkerDAO().startReinWorld(b, p, finalI);
                    player.closeInventory();
                    Location location;
                    location = b.getAttackerSpawn();
                    location.setWorld(Bukkit.getWorld(world));
                    if(b.getAttackerSpawn() == null) {
                        location = new Location(Bukkit.getWorld(world), 0, 64, 0);
                    }
                    player.teleport(location);
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(ChatColor.GOLD + "BunkerWorld for Bunker: " + ChatColor.DARK_PURPLE + b.getName()
                            + ChatColor.GOLD + " created by " + ChatColor.DARK_PURPLE + b.getAuthor() + ChatColor.GOLD
                            + " was successfully loaded.");
                    player.sendTitle(ChatColor.GOLD + "Entered " + ChatColor.DARK_PURPLE + b.getName(),
                            ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + b.getAuthor());
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                }
            };
            scaleGui.addSlot(scaleClick);
        }
        scaleGui.showInventory(p);
    }
}
