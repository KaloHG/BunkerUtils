package moe.kayla.bunkerutils.gui;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Bunker;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.gui.Clickable;
import vg.civcraft.mc.civmodcore.inventory.gui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;

/**
 * @Author Kayla
 * CreateGui Class File
 */
public class CreateGui {
    public static boolean kitsToggle;

    public static boolean isKitsToggled() {
        return kitsToggle;
    }

    /**
     * Opens an arena creation guided user interface.
     * @param player - Player that is opening the user interface.
     */
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
                protected void clicked(Player player) { openLoadKitsToggleGui(player, b);
                }
            };
            createGui.addSlot(clickBunker);
        }
        createGui.showInventory(player);
    }

    /**
     * Toggles using /inv load in the arena
     * @param player - player that opened the interface
     * @param b - bunker from the previous interface
     */
    public void openLoadKitsToggleGui(Player player, Bunker b){
        ClickableInventory kitsToggleGui = new ClickableInventory(9, "Toggle Loading Kits?");

        for(int i = 1; i < 3; i++){
            ItemStack is = new ItemStack(Material.REDSTONE_BLOCK);
            is.setAmount(1);
            int iterator = i;
            ItemUtils.setDisplayName(is,ChatColor.RED + "TURN /inv load OFF");
            if(iterator == 2){
                ItemUtils.setDisplayName(is,ChatColor.GREEN + "TURN /inv load ON");
            }
            Clickable toggleKits = new Clickable(is) {
                @Override
                protected void clicked(Player player) {
                    if(is.getItemMeta().getDisplayName().contains("OFF")){
                        kitsToggle = true;
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.sendMessage(ChatColor.RED + "/inv load is DISABLED in "+ b.getName());
                        }
                    }
                    if(is.getItemMeta().getDisplayName().contains("ON")){
                        kitsToggle = false;
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.sendMessage(ChatColor.GREEN + "/inv load is ENABLED in "+ b.getName());
                        }
                    }
                    openScalingGui(player, b);
                }
            };
            kitsToggleGui.addSlot(toggleKits);
        }
        kitsToggleGui.showInventory(player);
    }


    /**
     * Opens up a scaling guided user interface, opened after a bunker is selected.
     * @param p - Player that is opening the guided user interface.
     * @param b - the bunker that was selected.
     */
    public void openScalingGui(Player p, Bunker b) {
        ClickableInventory scaleGui = new ClickableInventory(9, "Citadel Scaling");

        for(int i = 1; i < 10; i++) {
            ItemStack is = new ItemStack(Material.GOLD_BLOCK);
            is.setAmount(1);
            int iter = i;

            if(b.getName().equals("Kyiv") && iter == 9){
                continue;
            }
            ItemUtils.setDisplayName(is, ChatColor.GOLD + "x" + i);
            ItemUtils.setLore(is, ChatColor.GRAY + "This number will be multiplied by the damage",
                    ChatColor.GRAY + "applied to a reinforcement in order to scale the reinforcement.",
                    ChatColor.GRAY + "E.x. Each Reinforcement break is 1 damage normally",
                    ChatColor.GRAY + "but now it is 1*"+ i + " which makes it " + i  + " damage.",
                    ChatColor.DARK_GRAY + "This value also applies to bastion damage.");
            int finalI = i;
            if(BunkerUtils.INSTANCE.bunkerDAO.isArenaLoading) return;
            Clickable scaleClick = new Clickable(is) {
                @Override
                protected void clicked(Player player) {
                    player.closeInventory();
                    String world = null;
                    try {
                        world = BunkerUtils.INSTANCE.getBunkerDAO().startReinWorld(b, p, finalI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            scaleGui.addSlot(scaleClick);
        }
        scaleGui.showInventory(p);
    }

}
