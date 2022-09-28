package moe.kayla.bunkerutils.model.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author Kayla
 * Team Class File
 */
public class Team {
    private TeamType teamType;
    private List<UUID> players;

    /**
     * Team Constructor for Arena's
     * @param tt - The Type of Team, reference TeamType Enum.
     */
    public Team(TeamType tt) {
        teamType = tt;
        players = new ArrayList<>();
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        if(players.contains(player.getUniqueId())) {
            players.remove(player.getUniqueId());
        }
    }

    //Intended for use when interacting with offline players.
    public void removeUuid(UUID uid) {
        if(players.contains(uid)) {
            players.remove(uid);
        }
    }

    /**
     * Sends a message to all team-members.
     * @param msg - the message to be sent.
     */
    public void sendTeamMessage(String msg) {
        for(UUID u : players) {
            //Redundant iirc
            if(Bukkit.getOfflinePlayer(u).isOnline()) {
                Player p = Bukkit.getPlayer(u);
                p.sendMessage(msg);
            }
        }
    }
}
