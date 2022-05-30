package moe.kayla.bunkerutils.listener;

import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class ArenaTickListener {

    /**
     * Holy fuck civcode is completely retarded... this handles opening arenas in a way that doesn't crash the server
     * kinda obsolete now but for any future end users: You cannot make citadel reinforcements in async because it
     * interfaces with bukkit. this method listens to ticks and limits the database pulls at any one time during arena
     * creation
     * todo: try handling arenas in a more elegant way >.<
     */

    public static void creationTickListener(){
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    BunkerUtils.INSTANCE.bunkerDAO.arenaTick();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(BunkerUtils.INSTANCE, 0L, 1L);
    }

    public static void cleanUpTask(){
        new BukkitRunnable(){
            @Override
            public void run() {
                BunkerUtils.INSTANCE.cleanUpWorlds();
            }
        }.runTaskTimer(BunkerUtils.INSTANCE, 0L, 3600L);
    }


}
