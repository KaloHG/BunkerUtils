package moe.kayla.bunkerutils.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kayla
 * ArenaManager Class File
 */
public class ArenaManager {
    private List<Arena> arenas = new ArrayList<>();

    public List<Arena> getArenas() {
        return arenas;
    }

    public Arena getArenaByHost(String name) {
        for(Arena arena : arenas) {
            if(arena.getHost().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void addArena(Arena arena) {
        arenas.add(arena);
    }
}
