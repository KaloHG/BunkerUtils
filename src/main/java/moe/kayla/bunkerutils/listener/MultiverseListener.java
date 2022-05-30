package moe.kayla.bunkerutils.listener;

import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import isaac.bastion.Bastion;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import vg.civcraft.mc.civmodcore.CivModCorePlugin;

/**
 * @Author Kayla
 * MultiverseListener Class File
 *
 * "Why Does This Exist?" - This class is an ATTEMPT, to try to stabilize the Civ-Plugin Suite with Multiverse world creation
 * this is due to Civ Plugins not listening for world loading... bastion bugs, etc. Due to Multiverse not having enough events
 * to listen for, notably a MVWorldCreationEvent (but for some reason having a MVWorldDeletionEvent) this relies on pathwork
 * glue and duct tape.
 */
public class MultiverseListener implements Listener {

    /**
     * Teleportation Event Handler
     * @param event - MVTeleportEvent, from Multiverse-Core.
     */
    @EventHandler
    public void schizoTeleport(MVTeleportEvent event) {
        //World Equals -1, Its not instantiated so we must initialize it for CMC and Bastion otherwise the server will turn schizophrenic.
        World world = event.getDestination().getLocation(event.getTeleportee()).getWorld();
        if(CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(world) == -1) {
            CivModCorePlugin.getInstance().getWorldIdManager().registerWorld(event.getDestination().getLocation(event.getTeleportee()).getWorld());
            CivModCorePlugin.getInstance().getChunkMetaManager().registerWorld(CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(world), world);
            BunkerUtils.INSTANCE.getLogger().info("Forcibly registered MultiVerse as a CivModCore World under ID: " + CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(world));
            Bastion.getBastionStorage().loadBastions();
            BunkerUtils.INSTANCE.getLogger().info(ChatColor.RED + "Forcibly reloaded Bastion storage to reset world cache. (CAUTION)");
        }
    }
}
