package moe.kayla.bunkerutils.listener;

import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import isaac.bastion.Bastion;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import vg.civcraft.mc.civmodcore.CivModCorePlugin;

public class MultiverseListener implements Listener {

    @EventHandler
    public void schizoTeleport(MVTeleportEvent event) {
        //World Equals -1, Its not instantiated so we must initialize it for CMC and Bastion otherwise the server will turn schizophrenic.
        World world = event.getDestination().getLocation(event.getTeleportee()).getWorld();
        if(CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldIdByName(world.getName()) == -1) {
            CivModCorePlugin.getInstance().getWorldIdManager().registerWorld(event.getDestination().getLocation(event.getTeleportee()).getWorld());
            CivModCorePlugin.getInstance().getChunkMetaManager().registerWorld(CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(world), world);
            BunkerUtils.INSTANCE.getLogger().info("Forcibly registered MultiVerse as a CivModCore World under ID: " + CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldIdByName(world.getName()));
            Bastion.getBastionStorage().loadBastions();
            BunkerUtils.INSTANCE.getLogger().info(ChatColor.RED + "Forcibly reloaded Bastion storage to reset world cache. (CAUTION)");
        }
    }
}
