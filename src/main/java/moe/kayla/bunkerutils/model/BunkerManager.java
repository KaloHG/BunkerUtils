package moe.kayla.bunkerutils.model;

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
