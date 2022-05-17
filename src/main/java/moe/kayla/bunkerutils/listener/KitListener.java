package moe.kayla.bunkerutils.listener;

import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.gui.CreateGui;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class KitListener implements Listener {

    /**
     * Hacky way to disable /inv loading in arenas
     * todo: make this more elegant
     * @param event
     */
    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event){
        if(BunkerUtils.INSTANCE.getArenaManager().isPlayerInArena(event.getPlayer()) && CreateGui.isKitsToggled()){
            if(event.getMessage().startsWith("/inv")){
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot load kits in this arena!");
                event.setCancelled(true);
            }
        }
    }
}
