package moe.kayla.bunkerutils.model;

import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.PearlFreeReason;
import moe.kayla.bunkerutils.BunkerUtils;
import moe.kayla.bunkerutils.model.arena.Team;
import moe.kayla.bunkerutils.model.arena.TeamType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author Kayla
 * Arena Class File
 */
public class Arena {
    private String world;
    private String host;
    private Bunker bunker;
    private Team defenders;
    private Team attackers;
    private Team pearled;
    private int ctDebuff;

    /**
     * Arena Constructor, mainly used in the CreateGui class.
     * @param world - World that will be the arena.
     * @param host - The username of the player who created the arena.
     * @param bunker - The bunker map that is being used for the arena.
     * @param ctDebuff - The citadel break modifier for the map.
     */
    public Arena(String world, String host, Bunker bunker, int ctDebuff) {
        this.world = world;
        this.host = host;
        this.bunker = bunker;
        this.ctDebuff = ctDebuff;
        this.defenders = new Team(TeamType.DEFENDERS);
        this.attackers = new Team(TeamType.ATTACKERS);
        this.pearled = new Team(TeamType.PEARLED);
    }

    public String getWorld() {
        return world;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Multiplier by which damage to reinforcements is multiplied by.
     * @return - Multiplier
     */
    public int getCtDebuff() {
        return ctDebuff;
    }

    public Bunker getBunker() {
        return bunker;
    }

    public Location getAttackerSpawn() {
        Location loc = bunker.getAttackerSpawn();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Location getDefenderSpawn() {
        Location loc = bunker.getDefenderSpawn();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Location getAttackerBeacon() {
        Location loc = bunker.getAttackerBeacon();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Location getDefenderBeacon() {
        Location loc = bunker.getDefenderBeacon();
        if(loc == null) { return null; }
        loc.setWorld(Bukkit.getWorld(world));
        return loc;
    }

    public Team getDefenders() {
        return defenders;
    }

    public Team getAttackers() {
        return attackers;
    }

    public Team getPearled() {
        return pearled;
    }

    //Core Teams
    public boolean isPlayerInTeam(Player p) {
        if(defenders.getPlayers().contains(p.getUniqueId())) { return true; }
        if(attackers.getPlayers().contains(p.getUniqueId())) { return true; }
        return false;
    }

    //Get team
    public TeamType getPlayerTeamType(Player player) {
        if(attackers.getPlayers().contains(player.getUniqueId())) {
            return TeamType.ATTACKERS;
        } else if(defenders.getPlayers().contains(player.getUniqueId())) {
            return TeamType.DEFENDERS;
        }
        return TeamType.INVALID;
    }

    public boolean isPlayerPearled(Player p) {
        if(pearled.getPlayers().contains(p)) { return true; }
        return false;
    }

    public int getTotalPlayers() {
        return defenders.getPlayers().size() + attackers.getPlayers().size();
    }

    public List<UUID> getAllPlayers() {
        List<UUID> combinedList = new ArrayList<>();
        combinedList.addAll(defenders.getPlayers());
        combinedList.addAll(attackers.getPlayers());
        return combinedList;
    }

    public void stripPlayerFromTeams(UUID p) {
        defenders.removeUuid(p);
        attackers.removeUuid(p);
    }

    public boolean isPlayerInWorld(Player p) {
        return p.getWorld().getName().equals(world);
    }

    public void cleanPlayers() {
        for(UUID uid : getAllPlayers()) {
            if(Bukkit.getOfflinePlayer(uid).isOnline()) {
                if(!isPlayerInWorld(Bukkit.getPlayer(uid))) {
                    if(!pearled.getPlayers().contains(uid)) {
                        //Remove player, no longer in world so we GET them. :)
                        stripPlayerFromTeams(uid);
                    }
                }
            } else {
                stripPlayerFromTeams(uid);
            }
        }
    }

    /**
     * Goodbye Function :(
     *
     * Removes the arena from the active list, and frees all pearls and unloads the world.
     */
    public boolean close() {
        try {
            BunkerUtils.INSTANCE.getLogger().warning("Starting Arena Closure for " + host + " on " + bunker.getName());
            Bukkit.broadcastMessage(ChatColor.GOLD + ChatColor.MAGIC.toString() + "EEEE" + ChatColor.RESET + " " + ChatColor.AQUA + bunker.getName()
                    + ChatColor.GOLD + " Hosted By: " + ChatColor.AQUA + host + ChatColor.GOLD + " is being closed. " + ChatColor.MAGIC + "EEEE");

            BunkerUtils.INSTANCE.getArenaManager().removeArena(this);
            pearled.sendTeamMessage(ChatColor.GREEN + "Arena closed, freeing players.");
            for (UUID uid : pearled.getPlayers()) {
                if (ExilePearlPlugin.getApi().isPlayerExiled(uid)) {
                    //player pearled, free.
                    ExilePearlPlugin.getApi().freePearl(ExilePearlPlugin.getApi().getPearl(uid), PearlFreeReason.FREED_BY_ADMIN);
                    BunkerUtils.INSTANCE.getLogger().info("Freeing Arena Player: " + Bukkit.getOfflinePlayer(uid).getName());
                }
            }
            for (Player p : Bukkit.getWorld(world).getPlayers()) {
                p.sendMessage(ChatColor.RED + "Arena is closing, trying to teleport you to spawn...");
                if (!p.performCommand("/spawn")) {
                    p.sendMessage("Spawn command failed, try leaving and rejoining?");
                }
            }
            BunkerUtils.INSTANCE.getLogger().info("World Unload Begun.");
            BunkerUtils.INSTANCE.getMvCore().getMVWorldManager().unloadWorld(world);

            BunkerUtils.INSTANCE.getLogger().info("Arena has now been de-registered from all references, closure successful.");
            BunkerUtils.INSTANCE.getLogger().info(ChatColor.AQUA + host + ChatColor.GOLD + "'s arena is now closed.");
            BunkerUtils.INSTANCE.sendArenaClosureMessage(this);
            return true;
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().warning("FAILED TO SAVE HOST ARENA: " + host);
            e.printStackTrace();
            return false;
        }
    }
}