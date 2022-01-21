package moe.kayla.bunkerutils.model;

import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

    /**
     * Adds an Arena to the Manager and initializes it.
     * @param arena - arena to be added.
     */
    public void addArena(Arena arena) {
        arenas.add(arena);
        if(arena.getAttackerBeacon() != null && arena.getDefenderBeacon() != null) {
            BunkerUtils.INSTANCE.getLogger().info("Newly created arena: " + arena.getHost() + " has bunker-beacons enabled. Initializing.");
            arena.getDefenderBeacon().getBlock().setType(Material.EMERALD_BLOCK);
            arena.getAttackerBeacon().getBlock().setType(Material.REDSTONE_BLOCK);
            Block defBec = arena.getDefenderBeacon().getBlock();
            Block atkBec = arena.getAttackerBeacon().getBlock();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(BunkerUtils.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    if(defBec.getLocation().getChunk().isLoaded()) {
                        Location loc = defBec.getLocation();
                        //Chunk is loaded, generate our particles.
                        for(int i = 0; i < 256; i++) {
                            loc.setY(i);
                            loc.getWorld().spawnParticle(Particle.WATER_BUBBLE, loc, 2);
                        }
                    }
                    if(atkBec.getLocation().getChunk().isLoaded()) {
                        Location loc = atkBec.getLocation();
                        //Chunk is loaded, generate our particles.
                        for(int i = 0; i < 256; i++) {
                            loc.setY(i);
                            loc.getWorld().spawnParticle(Particle.WATER_BUBBLE, loc, 2);
                        }
                    }
                }
            }, 1000, 1000);
        }
    }
}
