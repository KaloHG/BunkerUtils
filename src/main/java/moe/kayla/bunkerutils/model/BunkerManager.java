package moe.kayla.bunkerutils.model;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kayla
 * BunkerManager Class File
 */
public class BunkerManager {
    List<Bunker> bunkers = new ArrayList<>();

    public List<Bunker> getBunkers() {
        return bunkers;
    }

    public Bunker fetchBunkerByName(String name) {
        for(Bunker bunker : bunkers) {
            //Case in-sensitive
            if(bunker.getName().toLowerCase().equals(name)) {
                return bunker;
            }
        }
        return null;
    }

    public Bunker fetchBunkerByWorld(World world) {
        for(Bunker bunker : bunkers) {
            if(world.getName().equals(bunker.getWorld())) {
                return bunker;
            }
        }
        return null;
    }

    public boolean inBunkerWorld(World world) {
        for(Bunker bunker : bunkers) {
            if(world.getName().equals(bunker.getWorld())) {
                return true;
            }
        }
        return false;
    }

    public void addBunker(Bunker bunker) {
        bunkers.add(bunker);
    }

    /**
     *
     * @param bunkers - Bunkers loaded from DB.
     */
    public void setBunkers(List<Bunker> bunkers) {
        this.bunkers = bunkers;
    }
}
