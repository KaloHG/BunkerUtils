package moe.kayla.bunkerutils.model;

import org.bukkit.Location;

import java.util.UUID;

/**
 * @Author Kayla
 * Bunker Object Class File
 *
 * There is no need to javadoc half the functions in this class. They are self-explanatory.
 */
public class Bunker {
    private UUID uuid;
    private String name;
    private String world;
    private String author;
    private String description;
    private Location defenderSpawn;
    private Location attackerSpawn;
    private Location defenderBeacon;
    private Location attackerBeacon;

    /**
     * DB Import Constructor.
     * @param uuid - UUID For the Bunker
     * @param name - Name for the bunker map
     * @param author - Author of the Bunker Map
     * @param description - Description for Bunker Map.
     */
    public Bunker(UUID uuid, String name, String world, String author, String description, Location defenderSpawn,
                  Location attackerSpawn, Location defenderBeacon, Location attackerBeacon) {
        this.uuid = uuid;
        this.name = name;
        this.author = author;
        this.description = description;
        this.world = world;
        this.defenderSpawn = defenderSpawn;
        this.attackerSpawn = attackerSpawn;
        this.defenderBeacon = defenderBeacon;
        this.attackerBeacon = attackerBeacon;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getWorld() {
        return world;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefenderSpawn(Location loc) {
        defenderSpawn = loc;
    }

    public Location getDefenderSpawn() {
        return defenderSpawn;
    }

    public void setAttackerSpawn(Location attackerSpawn) {
        this.attackerSpawn = attackerSpawn;
    }

    public Location getAttackerSpawn() {
        return attackerSpawn;
    }

    public void setAttackerBeacon(Location attackerBeacon) {
        this.attackerBeacon = attackerBeacon;
    }

    public void setDefenderBeacon(Location defenderBeacon) {
        this.defenderBeacon = defenderBeacon;
    }

    public Location getDefenderBeacon() {
        return defenderBeacon;
    }

    public Location getAttackerBeacon() {
        return attackerBeacon;
    }
}
