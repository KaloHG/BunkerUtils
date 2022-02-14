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

    /**
     * Bunker List getter function
     * @return - Bunkers loaded onto BunkerUtils
     */
    public List<Bunker> getBunkers() {
        return bunkers;
    }

    /**
     * Fetches a bunker by its name
     * @param name - The name of the bunker that is being looked for
     * @return - The bunker if present, otherwise null.
     */
    public Bunker fetchBunkerByName(String name) {
        for(Bunker bunker : bunkers) {
            //Case in-sensitive
            if(bunker.getName().toLowerCase().equals(name)) {
                return bunker;
            }
        }
        return null;
    }

    /**
     * Fetches bunker based off of the world.
     * @param world - world to be bunkerchecked
     * @return - the bunker if located, otherwise null.
     */
    public Bunker fetchBunkerByWorld(World world) {
        for(Bunker bunker : bunkers) {
            if(world.getName().equals(bunker.getWorld())) {
                return bunker;
            }
        }
        return null;
    }

    /**
     * Returns whether or not a world is a bunker world.
     * @param world - the world to be checked
     * @return - whether the world is a bunker world or not.
     */
    public boolean inBunkerWorld(World world) {
        for(Bunker bunker : bunkers) {
            if(world.getName().equals(bunker.getWorld())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Bunker addition function, used to load bunkers into memory when created.
     * @param bunker - the bunker to be added to bunker list.
     */
    public void addBunker(Bunker bunker) {
        bunkers.add(bunker);
    }

    /**
     * Bunker Loading function, usually intended for DB loading
     * @param bunkers - Bunkers loaded from DB.
     */
    public void setBunkers(List<Bunker> bunkers) {
        this.bunkers = bunkers;
    }
}
