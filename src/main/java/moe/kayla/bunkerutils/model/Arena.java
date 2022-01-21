package moe.kayla.bunkerutils.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @Author Kayla
 * Arena Class File
 */
public class Arena {
    private String world;
    private String host;
    private Bunker bunker;
    private int ctDebuff;

    public Arena(String world, String host, Bunker bunker, int ctDebuff) {
        this.world = world;
        this.host = host;
        this.bunker = bunker;
        this.ctDebuff = ctDebuff;
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
}
