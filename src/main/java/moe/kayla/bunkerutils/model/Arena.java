package moe.kayla.bunkerutils.model;

import com.devotedmc.ExilePearl.ExilePearlApi;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.PearlFreeReason;
import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.arena.Team;
import moe.kayla.bunkerutils.model.arena.TeamType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @Author Kayla
 * Arena Class File
 */
public class Arena {
    private String world;
    private String host;
    private Bunker bunker;
    private Team defenders;
    private Team attackers;
    private Team pearled;
    private int ctDebuff;

    public Arena(String world, String host, Bunker bunker, int ctDebuff) {
        this.world = world;
        this.host = host;
        this.bunker = bunker;
        this.ctDebuff = ctDebuff;
        this.defenders = new Team(TeamType.DEFENDERS);
        this.attackers = new Team(TeamType.ATTACKERS);
        this.pearled = new Team(TeamType.PEARLED);
    }

    public String getWorld() {
        return world;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Multiplier by which damage to reinforcements is multiplied by.
     * @return - Multiplier
     */
    public int getCtDebuff() {
        return ctDebuff;
    }

    public Bunker getBunker() {
        return bunker;
    }

    public Location getAttackerSpawn() {
        Location loc = bunker.getAttackerSpawn();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Location getDefenderSpawn() {
        Location loc = bunker.getDefenderSpawn();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Location getAttackerBeacon() {
        Location loc = bunker.getAttackerBeacon();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Location getDefenderBeacon() {
        Location loc = bunker.getDefenderBeacon();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Team getDefenders() {
        return defenders;
    }

    public Team getAttackers() {
        return attackers;
    }

    public Team getPearled() {
        return pearled;
    }

    //Core Teams
    public boolean isPlayerInTeam(Player p) {
        if(defenders.getPlayers().contains(p.getUniqueId())) { return true; }
        if(attackers.getPlayers().contains(p.getUniqueId())) { return true; }
        return false;
    }

    public boolean isPlayerPearled(Player p) {
        if(pearled.getPlayers().contains(p)) { return true; }
        return false;
    }

    public void stripPlayerFromTeams(Player p) {
        defenders.removePlayer(p);
        attackers.removePlayer(p);
        pearled.removePlayer(p);
    }

    /**
     * Goodbye Function :(
     */
    public boolean close() {
        try {
            BunkerUtils.INSTANCE.getLogger().warning("Starting Arena Closure for " + host + " on " + bunker.getName());
            Bukkit.broadcastMessage(ChatColor.GOLD + ChatColor.MAGIC.toString() + "EEEE" + ChatColor.RESET + " " + ChatColor.AQUA + bunker.getName()
                    + ChatColor.GOLD + " Hosted By: " + ChatColor.AQUA + host + ChatColor.GOLD + "is being closed. " + ChatColor.MAGIC + "EEEE");

            pearled.sendTeamMessage(ChatColor.GREEN + "Arena closed, freeing players.");
            for (UUID uid : pearled.getPlayers()) {
                if (ExilePearlPlugin.getApi().isPlayerExiled(uid)) {
                    //player pearled, free.
                    ExilePearlPlugin.getApi().freePearl(ExilePearlPlugin.getApi().getPearl(uid), PearlFreeReason.FREED_BY_ADMIN);
                    BunkerUtils.INSTANCE.getLogger().info("Freeing Arena Player: " + Bukkit.getOfflinePlayer(uid).getName());
                }
            }
            for (Player p : Bukkit.getWorld(world).getPlayers()) {
                p.sendMessage(ChatColor.RED + "Arena is closing, trying to teleport you to spawn...");
                if (!p.performCommand("/spawn")) {
                    p.sendMessage("Spawn command failed, try leaving and rejoining?");
                }
            }
            BunkerUtils.INSTANCE.getLogger().info("World Unload Begun.");
            BunkerUtils.INSTANCE.getMvCore().getMVWorldManager().unloadWorld(world);

            BunkerUtils.INSTANCE.getArenaManager().removeArena(this);
            BunkerUtils.INSTANCE.getLogger().info("Arena has now been de-registered from all references, closure successful.");
            BunkerUtils.INSTANCE.getLogger().info(ChatColor.AQUA + host + ChatColor.GOLD + "'s arena is now closed.");
            return true;
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().warning("FAILED TO SAVE HOST ARENA: " + host);
            e.printStackTrace();
            return false;
        }
    }
}