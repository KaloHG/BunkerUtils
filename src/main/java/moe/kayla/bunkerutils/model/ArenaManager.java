package moe.kayla.bunkerutils.model;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

    public List<World> activeArenaWorlds() {
        List<World> newWorldList = new ArrayList<>();
        for(Arena a : arenas) {
            newWorldList.add(Bukkit.getWorld(a.getWorld()));
        }
        return newWorldList;
    }

    public Arena getArenaByWorld(World world) {
        for(Arena a : arenas) {
            if(Bukkit.getWorld(a.getWorld()).equals(world)) {
                return a;
            }
        }
        return null;
    }

    public boolean isPlayerInArena(Player player) {
        return activeArenaWorlds().contains(player.getWorld());
    }

    public void addArena(Arena arena) {
        arenas.add(arena);
    }
}
