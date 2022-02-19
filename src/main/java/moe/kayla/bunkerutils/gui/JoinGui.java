package moe.kayla.bunkerutils.gui;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.Arena;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.namelayer.GroupManager;

import java.util.ArrayList;
import java.util.List;

public class JoinGui {

    public void openJoinGui(Player p) {
        ClickableInventory joinGui = new ClickableInventory(18, "Join an Arena");

        int i = 18;
        for(Arena a : BunkerUtils.INSTANCE.arenaManager.getArenas()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(a.getHost());
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName(ChatColor.GOLD + a.getHost() + "'s Arena");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "Map: " + ChatColor.AQUA + a.getBunker().getName());
            lore.add(ChatColor.DARK_GREEN + "Defenders" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + a.getDefenders().getPlayers().size());
            lore.add(ChatColor.DARK_RED + "Attackers" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + a.getAttackers().getPlayers().size());
            lore.add(ChatColor.LIGHT_PURPLE + "Pearled" + ChatColor.DARK_GREEN + ": " + ChatColor.AQUA + a.getPearled().getPlayers().size());
            lore.add(ChatColor.GOLD + "Players" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + a.getTotalPlayers());
            skullMeta.setLore(lore);
            playerHead.setItemMeta(skullMeta);
            Clickable click = new Clickable(playerHead) {
                @Override
                protected void clicked(Player player) {
                    p.closeInventory(); //Close inv before opening new one just in-case.
                    openTeamGui(p, a);
                }
            };
            joinGui.addSlot(click);
            i = i - 1;
        }

        for(int j = 0; j < i; j++) {
                ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemUtils.setDisplayName(stack, ChatColor.RED + "Empty Slot");
                Clickable click = new DecorationStack(stack);
                joinGui.addSlot(click);
        }
        joinGui.showInventory(p);
    }

    public void openTeamGui(Player p, Arena a) {
        ClickableInventory teamGui = new ClickableInventory(9, "Select a Team");

        int[] decStacks = {0, 1, 2, 4, 6, 7, 8};

        ItemStack attackStack = new ItemStack(Material.RED_WOOL);
        ItemUtils.setDisplayName(attackStack, ChatColor.RED + "Attackers");

        ItemStack defenseStack = new ItemStack(Material.GREEN_WOOL);
        ItemUtils.setDisplayName(defenseStack, ChatColor.DARK_GREEN + "Defenders");

        Clickable attackClick = new Clickable(attackStack) {
            @Override
            protected void clicked(Player player) {
                if(a.getBunker().getAttackerSpawn() == null) {
                    player.sendMessage(ChatColor.RED + "The attacker spawn for this arena is not set! Contact an administrator.");
                    player.closeInventory();
                    return;
                }
                //Remove Attacker from Defender Group.
                GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getDefenderGroup()).removeMember(player.getUniqueId());
                GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getAttackerGroup()).addMember(player.getUniqueId(), GroupManager.PlayerType.MODS);
                GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getAttackerGroup()).setDefaultGroup(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Your citadel default group has been set to " + ChatColor.RED + "Attackers");
                if(a.isPlayerInTeam(player)) {
                    a.stripPlayerFromTeams(player.getUniqueId());
                }
                a.getAttackers().addPlayer(player);
                player.sendMessage(ChatColor.GOLD + "You have successfully been added to the " + ChatColor.RED + "Attackers " + ChatColor.GOLD + "team.");
                Location attackerSpawn = a.getBunker().getAttackerSpawn();
                player.sendTitle(ChatColor.GOLD + "Joined " + ChatColor.DARK_PURPLE + a.getBunker().getName(),
                        ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + a.getBunker().getAuthor());
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                player.teleport(attackerSpawn);
                player.setDisplayName(ChatColor.DARK_RED + player.getName());
                player.setPlayerListName(ChatColor.DARK_RED + player.getName());
            }
        };
        teamGui.setSlot(attackClick, 3);
        Clickable defenseClick = new Clickable(defenseStack) {
            @Override
            protected void clicked(Player player) {
                if(a.getBunker().getDefenderSpawn() == null) {
                    player.sendMessage(ChatColor.RED + "The defender spawn for this arena is not set! Contact an administrator.");
                    player.closeInventory();
                    return;
                }
                //Remove Defender from Attacker Group.
                GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getAttackerGroup()).removeMember(player.getUniqueId());
                GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getDefenderGroup()).addMember(player.getUniqueId(), GroupManager.PlayerType.MODS);
                GroupManager.getGroup(BunkerUtils.INSTANCE.getBunkerConfiguration().getDefenderGroup()).setDefaultGroup(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Your citadel default group has been set to " + ChatColor.DARK_GREEN + "Defenders");
                if(a.isPlayerInTeam(player)) {
                    a.stripPlayerFromTeams(player.getUniqueId());
                }
                a.getDefenders().addPlayer(player);
                player.sendMessage(ChatColor.GOLD + "You have successfully been added to the " + ChatColor.DARK_GREEN + "Defenders " + ChatColor.GOLD + "team.");
                Location defenderSpawn = a.getBunker().getDefenderSpawn();
                defenderSpawn.setWorld(Bukkit.getWorld(a.getWorld()));
                player.teleport(defenderSpawn);
                player.sendTitle(ChatColor.GOLD + "Joined " + ChatColor.DARK_PURPLE + a.getBunker().getName(),
                        ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + a.getBunker().getAuthor());
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                player.setDisplayName(ChatColor.DARK_GREEN + player.getName());
                player.setPlayerListName(ChatColor.DARK_GREEN + player.getName());
            }
        };
        teamGui.setSlot(defenseClick, 5);
        for(Integer i : decStacks) {
            ItemStack decStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemUtils.setDisplayName(decStack, "");
            Clickable dec = new DecorationStack(decStack);
            teamGui.setSlot(dec, i);
        }

        teamGui.showInventory(p);
    }
}
