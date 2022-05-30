package moe.kayla.bunkerutils.model;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import isaac.bastion.Bastion;
import isaac.bastion.BastionType;
import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.citadel.reinforcementtypes.ReinforcementType;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CivModCorePlugin;
import vg.civcraft.mc.civmodcore.dao.ConnectionPool;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;
import vg.civcraft.mc.civmodcore.utilities.CivLogger;

/**
 * @Author Kayla
 * BunkerDAO Class File
 */
public class BunkerDAO {
    public Boolean isArenaLoading = false;
    public PreparedStatement arenaLoadingStatement;
    public Connection arenaLoadingConnection;
    public ResultSet arenaLoadingResultSet;
    public int arenaLoadingNum;
    public Bunker arenaLoadingBunker;
    public String arenaLoadingWorldname;
    public Player player;
    public int scale;
    
    
    private ManagedDatasource dataSource;

    public BunkerDAO(ACivMod plugin, DatabaseCredentials credentials) {

        dataSource = ManagedDatasource.construct(plugin, credentials);
        prepareMigrations();
        dataSource.updateDatabase();
    }

    /**
     * Initialization Tables
     */
    private void prepareMigrations() {
        dataSource.registerMigration(0, false, "CREATE TABLE IF NOT EXISTS `bunker_info`" +
                "(`BunkerUUID` VARCHAR(50) NOT NULL,`BunkerName` VARCHAR(50) NOT NULL,`BunkerAuthor` VARCHAR(50) NOT NULL," +
                "`BunkerDescription` TEXT NOT NULL," +
                "`BunkerWorld` VARCHAR(50) NOT NULL," +
                "`dx` BIGINT NULL, `dy` BIGINT NULL, `dz` BIGINT NULL," +
                "`ax` BIGINT NULL, `ay` BIGINT NULL, `az` BIGINT NULL," +
                " `dbx` BIGINT NULL, `dby` BIGINT NULL, `dbz` BIGINT NULL," +
                "`abx` BIGINT NULL, `aby` BIGINT NULL, `abz` BIGINT NULL);");
    }

    /**
     * Saves bunkers to the SQL database
     * @return - Whether the method succeeded.
     */
    public boolean saveBunkerList() {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement bunkerSaveStatement = conn.prepareStatement("insert into bunker_info" +
                    "(BunkerUUID, BunkerName, BunkerAuthor, BunkerDescription, BunkerWorld, dx, dy, dz, ax, ay, az, " +
                    "dbx, dby, dbz, abx, aby, abz)" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            PreparedStatement deleteBunkIfExists = conn.prepareStatement("delete from bunker_info where BunkerUUID = ?");
            for(Bunker bunker : BunkerUtils.INSTANCE.getBunkerManager().getBunkers()) {
                deleteBunkIfExists.setString(1, bunker.getUuid().toString());
                deleteBunkIfExists.execute();
                bunkerSaveStatement.setString(1, bunker.getUuid().toString());
                bunkerSaveStatement.setString(2, bunker.getName());
                bunkerSaveStatement.setString(3, bunker.getAuthor());
                bunkerSaveStatement.setString(4, bunker.getDescription());
                bunkerSaveStatement.setString(5, bunker.getWorld());
                if(bunker.getDefenderSpawn() != null) {
                    bunkerSaveStatement.setInt(6, (int) bunker.getDefenderSpawn().getX());
                    bunkerSaveStatement.setInt(7, (int) bunker.getDefenderSpawn().getY());
                    bunkerSaveStatement.setInt(8, (int) bunker.getDefenderSpawn().getZ());
                } else {
                    bunkerSaveStatement.setNull(6, Types.BIGINT);
                    bunkerSaveStatement.setNull(7, Types.BIGINT);
                    bunkerSaveStatement.setNull(8, Types.BIGINT);
                }
                if(bunker.getAttackerSpawn() != null) {
                    bunkerSaveStatement.setInt(9, (int) bunker.getAttackerSpawn().getX());
                    bunkerSaveStatement.setInt(10, (int) bunker.getAttackerSpawn().getY());
                    bunkerSaveStatement.setInt(11, (int) bunker.getAttackerSpawn().getZ());
                } else {
                    bunkerSaveStatement.setNull(9, Types.BIGINT);
                    bunkerSaveStatement.setNull(10, Types.BIGINT);
                    bunkerSaveStatement.setNull(11, Types.BIGINT);
                }
                if(bunker.getDefenderBeacon() != null) {
                    bunkerSaveStatement.setInt(12, bunker.getDefenderBeacon().getBlockX());
                    bunkerSaveStatement.setInt(13, bunker.getDefenderBeacon().getBlockY());
                    bunkerSaveStatement.setInt(14, bunker.getDefenderBeacon().getBlockZ());
                } else {
                    bunkerSaveStatement.setNull(12, Types.BIGINT);
                    bunkerSaveStatement.setNull(13, Types.BIGINT);
                    bunkerSaveStatement.setNull(14, Types.BIGINT);
                }
                if(bunker.getAttackerBeacon() != null) {
                    bunkerSaveStatement.setInt(15, bunker.getAttackerBeacon().getBlockX());
                    bunkerSaveStatement.setInt(16, bunker.getAttackerBeacon().getBlockY());
                    bunkerSaveStatement.setInt(17, bunker.getAttackerBeacon().getBlockZ());
                } else {
                    bunkerSaveStatement.setNull(15, Types.BIGINT);
                    bunkerSaveStatement.setNull(16, Types.BIGINT);
                    bunkerSaveStatement.setNull(17, Types.BIGINT);
                }
                bunkerSaveStatement.execute();
            }
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("Failed to save BunkerList.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Loads bunkers from the SQL Database into memory.
     * @return - Whether the method succeeded.
     */

    public void removeBunker(Bunker bunker){
        Optional<Bunker> remove = BunkerUtils.INSTANCE.getBunkerManager().getBunkers().stream()
                .filter(x ->bunker.getName().equals(x.getName()))
                .findFirst();
        if(remove.isPresent()) {
            BunkerUtils.INSTANCE.getBunkerManager().getBunkers().remove(remove.get());
        }
    }
    public boolean loadBunkerList() {
        List<Bunker> bunkerList = new ArrayList<>();
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bunker_info;")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                UUID uid = UUID.fromString(rs.getString(1));
                String name = rs.getString(2);
                String author = rs.getString(3);
                String desc = rs.getString(4);
                String world = rs.getString(5);
                Location defLoc;
                //Default return value for null values for the getInt() method is zero.
                if(rs.getInt(6) != 0) {
                    defLoc = new Location(Bukkit.getWorld(world), rs.getInt(6), rs.getInt(7), rs.getInt(8));
                } else {
                    defLoc = null;
                }
                Location atkLoc;
                if(rs.getInt(9) != 0) {
                    atkLoc = new Location(Bukkit.getWorld(world), rs.getInt(9), rs.getInt(10), rs.getInt(11));
                } else {
                    atkLoc = null;
                }
                Location defBea;
                if(rs.getInt(12) != 0) {
                    defBea = new Location(Bukkit.getWorld(world), rs.getInt(12), rs.getInt(13), rs.getInt(14));
                } else {
                    defBea = null;
                }
                Location atkBea;
                if(rs.getInt(15) != 0) {
                    atkBea = new Location(Bukkit.getWorld(world), rs.getInt(15), rs.getInt(16), rs.getInt(17));
                } else {
                    atkBea = null;
                }
                Bunker newBunk = new Bunker(uid, name, world, author, desc, defLoc, atkLoc, defBea, atkBea);
                bunkerList.add(newBunk);
            }
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("Failed to load bunker list, contact an administrator.");
            e.printStackTrace();
        }
        BunkerUtils.INSTANCE.getBunkerManager().setBunkers(bunkerList);
        return true;
    }

    /**
     * Saves a bunker and its reinforcements into a MySQL Table.
     * Thank you Okx with making this async
     * @param bunker - The bunker to be saved.
     * @return - Whether the method succeeded.
     */

    public CompletableFuture<Boolean> createNewReinWorld(Bunker bunker) {
        World world = Bukkit.getServer().getWorld(bunker.getWorld());
        int id = CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(world);
        //CivModCorePlugin.getInstance().getChunkMetaManager().flushPlugin();

        return CompletableFuture.supplyAsync(() -> {
                /**
                 * Citadel Export
                 */
                try (
                    Connection conn = dataSource.getConnection();
                    PreparedStatement prep = conn.prepareStatement(
                        "CREATE TABLE `bunker_" + bunker.getWorld() + "_reinforcements` ("
                            + "`x` INT NOT NULL,"
                            + "`y` INT NOT NULL,"
                            + "`z` INT NOT NULL,"
                            + "`material_id` INT NOT NULL,"
                            + "`durability` INT NOT NULL,"
                            + "`group_id` INT NOT NULL,"
                            + "`maturation_time` INT NOT NULL,"
                            + "`rein_type_id` INT NOT NULL);")) {
                    PreparedStatement p1 = conn.prepareStatement("DROP TABLE IF EXISTS `bunker_" + bunker.getWorld() + "_reinforcements`"); p1.execute();
                    PreparedStatement p2 = conn.prepareStatement("DROP TABLE IF EXISTS `bunker_" + bunker.getWorld() +"_bastions`"); p2.execute();
                    prep.execute();
                    //Forcibly Flush Citadel & Bastion Data to DB.
                    long currentTime = System.currentTimeMillis();
                    PreparedStatement loadStatement = conn.prepareStatement(
                        "SELECT * FROM ctdl_reinforcements WHERE world_id = " + id + ";");
                    PreparedStatement insertStatement = conn.prepareStatement(
                        "insert into bunker_" + bunker.getWorld()
                            + "_reinforcements(x, y, z, material_id, durability, group_id, maturation_time, rein_type_id) values (?,?,?,?,?,?,?,?);");
                    ResultSet rs = loadStatement.executeQuery();
                    int i = 0;
                    while (rs.next()) {
                        //Multiply the chunk value by 16, to get the location.
                        int x = ((rs.getInt(1) * 16) + rs.getInt(4));
                        int y = rs.getInt(5);
                        int z = ((rs.getInt(2) * 16) + rs.getInt(6));
                        int material_id = rs.getInt(7);
                        int dura = (int) rs.getFloat(8);
                        int group_id = rs.getInt(9);
                        int maturation_time = (int) rs.getTimestamp(11).getTime();
                        int rein_type_id = rs.getInt(7);
                        insertStatement.setInt(1, x);
                        insertStatement.setInt(2, y);
                        insertStatement.setInt(3, z);
                        insertStatement.setInt(4, material_id);
                        insertStatement.setInt(5, dura);
                        insertStatement.setInt(6, group_id);
                        insertStatement.setInt(7, maturation_time);
                        insertStatement.setInt(8, rein_type_id);
                        insertStatement.addBatch();
                        i++;
                    }
                    BunkerUtils.INSTANCE.getLogger()
                        .info("Batch 0: " + (System.currentTimeMillis() - currentTime) + " ms");
                    BunkerUtils.INSTANCE.getLogger().info("Batch 0 size: " + i);
                    insertStatement.executeBatch();
                    BunkerUtils.INSTANCE.getLogger()
                        .info("Batch Finish: " + (System.currentTimeMillis() - currentTime) + " ms");
                    PreparedStatement bunkerSaveStatement = conn.prepareStatement(
                        "insert into bunker_info" +
                            "(BunkerUUID, BunkerName, BunkerAuthor, BunkerDescription, BunkerWorld, dx, dy, dz, ax, ay, az, "
                            +
                            "dbx, dby, dbz, abx, aby, abz)" +
                            " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                    bunkerSaveStatement.setString(1, bunker.getUuid().toString());
                    bunkerSaveStatement.setString(2, bunker.getName());
                    bunkerSaveStatement.setString(3, bunker.getAuthor());
                    bunkerSaveStatement.setString(4, bunker.getDescription());
                    bunkerSaveStatement.setString(5, bunker.getWorld());
                    //Null until spawns are set.
                    bunkerSaveStatement.setNull(6, Types.BIGINT);
                    bunkerSaveStatement.setNull(7, Types.BIGINT);
                    bunkerSaveStatement.setNull(8, Types.BIGINT);
                    bunkerSaveStatement.setNull(9, Types.BIGINT);
                    bunkerSaveStatement.setNull(10, Types.BIGINT);
                    bunkerSaveStatement.setNull(11, Types.BIGINT);
                    bunkerSaveStatement.setNull(12, Types.BIGINT);
                    bunkerSaveStatement.setNull(13, Types.BIGINT);
                    bunkerSaveStatement.setNull(14, Types.BIGINT);
                    bunkerSaveStatement.setNull(15, Types.BIGINT);
                    bunkerSaveStatement.setNull(16, Types.BIGINT);
                    bunkerSaveStatement.setNull(17, Types.BIGINT);
                    bunkerSaveStatement.execute();
                } catch (Exception ex) {
                    BunkerUtils.INSTANCE.getLogger()
                        .severe("(CITADEL FAILURE) Failed to save BunkerWorld " + bunker.getWorld());
                    ex.printStackTrace();
                    return false;
                }
                /**
                 * Bastion Export
                 */
                try (Connection conn = dataSource.getConnection();
                    PreparedStatement prep = conn.prepareStatement(
                        "CREATE TABLE `bunker_" + bunker.getWorld()
                            + "_bastions`(`bastion_type` VARCHAR(50) NOT NULL," +
                            "`loc_x` INT NOT NULL," +
                            "`loc_y` INT NOT NULL," +
                            "`loc_z` INT NOT NULL);")) {
                    prep.execute();
                    PreparedStatement pullStatement = conn.prepareStatement(
                        "SELECT * FROM `bastion_blocks` WHERE loc_world = \"" + bunker.getWorld()
                            + "\";");
                    PreparedStatement insertStatement = conn.prepareStatement(
                        "INSERT INTO bunker_" + bunker.getWorld()
                            + "_bastions(bastion_type, loc_x, loc_y, loc_z) values (?,?,?,?);");
                    ResultSet rs = pullStatement.executeQuery();
                    while (rs.next()) {
                        String type = rs.getString(2);
                        int x = rs.getInt(3);
                        int y = rs.getInt(4);
                        int z = rs.getInt(5);
                        insertStatement.setString(1, type);
                        insertStatement.setInt(2, x);
                        insertStatement.setInt(3, y);
                        insertStatement.setInt(4, z);
                        insertStatement.execute();
                    }
                } catch (Exception e) {
                    BunkerUtils.INSTANCE.getLogger()
                        .severe("(BASTION FAILURE) Failed to save BunkerWorld " + bunker.getWorld());
                    e.printStackTrace();
                    return false;
                }
                return true;
        }).thenApplyAsync(success -> {
            if (success) {
                BunkerUtils.INSTANCE.getBunkerManager().addBunker(bunker);
            }
            return success;
        }, runnable -> Bukkit.getScheduler().runTask(BunkerUtils.INSTANCE, runnable));
    }

    /**
     * Defines the Local variables to be used within the project. Generates the world to be played on.
     * @param bunker - pulled from the CreateGui class
     * @param player - pulled from the CreateGui class
     * @param scale - pulled from the CreateGui class
     * @return
     * @throws Exception
     */
    public void loadWorld(Bunker bunker){
        Random r = new Random();

        Integer rn1= r.nextInt(9);
        Integer rn2= r.nextInt(9);
        Integer rn3= r.nextInt(9);
        char rc1= 'a';

        String arenaID = String.valueOf( rn1 + rn2 + rn3) + rc1;


        arenaLoadingWorldname = bunker.getWorld() + "_" + arenaID;

        if(BunkerUtils.INSTANCE.getMvCore().getCore().getMVWorldManager().cloneWorld(bunker.getWorld(), arenaLoadingWorldname)) {
            World aWorld = Bukkit.getWorld(arenaLoadingWorldname);
            CivModCorePlugin.getInstance().getWorldIdManager().registerWorld(aWorld);
            CivModCorePlugin.getInstance().getChunkMetaManager().registerWorld(CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(Bukkit.getServer().getWorld(arenaLoadingWorldname)),
                    Bukkit.getWorld(arenaLoadingWorldname));
            BunkerUtils.INSTANCE.getLogger().info("Forcibly registered CivModCore World under ID: " + CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldId(Bukkit.getServer().getWorld(arenaLoadingWorldname)));
        }
        BunkerUtils.INSTANCE.getLogger().info("MultiVerse world created with name: " + arenaLoadingWorldname);
    }

    public synchronized String startReinWorld(Bunker bunker, Player player, int scale) throws Exception {

        loadWorld(bunker);


        arenaLoadingConnection = dataSource.getConnection();
        Connection conn = arenaLoadingConnection;


        arenaLoadingStatement = conn.prepareStatement("SELECT * FROM bunker_"+bunker.getWorld()+"_reinforcements;");
        PreparedStatement ps = arenaLoadingStatement;


        arenaLoadingResultSet = ps.executeQuery();

        arenaLoadingResultSet.setFetchSize(1000);

        arenaLoadingNum = 0;
        isArenaLoading = true;
        arenaLoadingBunker = bunker;

        this.player = player;
        this.scale = scale;

        this.player.sendMessage(ChatColor.GOLD+ "Loading Arena please wait...");

        return arenaLoadingWorldname;
    }

    /**
     * This method is called in the ArenaCreationListener and helps limit the amount of database pulls by listening to the
     * ticks of the server. 1machinemaker1's hacky way of trying to maintain server stability.
     * If you experience issue with loading arena's change the iterator check to something lower than 100k
     * @throws SQLException
     */
    public void arenaTick() throws SQLException {
        if(!isArenaLoading) return;

        for (int i=0; i<100000; i++) {
            if (!arenaLoadingResultSet.next()) { finishLoadingArena(); return; }
            ResultSet rs = arenaLoadingResultSet;
            int x = rs.getInt(1);
            int y = rs.getInt(2);
            int z = rs.getInt(3);
            int material_id = rs.getInt(4);
            int dura = rs.getInt(5);
            int group_id = rs.getInt(6);
            int maturation_time = rs.getInt(7);
            int rein_type_id = rs.getInt(8);
            Location loc = new Location(Bukkit.getWorld(arenaLoadingWorldname), x, y, z);
            ReinforcementType reinType = BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getById((short) rein_type_id);
            Reinforcement newImport = new Reinforcement(loc, reinType, group_id, maturation_time, dura, false, false);
            BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().putReinforcement(newImport);
            arenaLoadingNum++;

        }
    }

    /**
     * Finishes loading the arena by closing the database connections and initializing Bastion creation
     * @throws SQLException
     */
    void finishLoadingArena() throws SQLException {

        arenaLoadingResultSet = null;
        arenaLoadingStatement.close(); arenaLoadingStatement = null;
        arenaLoadingConnection.close(); arenaLoadingConnection = null;
        isArenaLoading = false;
        //We forcibly run the loadBastions(); method in order to get Bastion to actually load the new worlds into its memory.
        Bastion.getBastionStorage().loadBastions();
        try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM bunker_" + arenaLoadingBunker.getWorld() + "_bastions")) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String type = rs.getString(1);
                int x = rs.getInt(2);
                int y = rs.getInt(3);
                int z = rs.getInt(4);
                Location loc = new Location(Bukkit.getWorld(arenaLoadingWorldname), x, y, z);
                //Fuck bastions static methods
                Bastion.getBastionStorage().createBastion(loc, BastionType.getBastionType(type), player);
                BunkerUtils.INSTANCE.getLogger().info("Creating new bastion at " + x + ", " + y + ", "+ z);
            }
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("[BASTION FAILURE] Failed to create a new bunker world.");
            e.printStackTrace();
        }
        BunkerUtils.INSTANCE.getArenaManager().addArena(new Arena(arenaLoadingWorldname, player.getName(), arenaLoadingBunker, scale));
        Bukkit.broadcastMessage(ChatColor.GOLD + "An arena on bunker " + ChatColor.DARK_PURPLE + arenaLoadingBunker.getName() + ChatColor.GOLD +
                " has been opened!");
        player.sendMessage(ChatColor.GOLD + "BunkerWorld for Bunker: " + ChatColor.DARK_PURPLE + arenaLoadingBunker.getName()
                + ChatColor.GOLD + " created by " + ChatColor.DARK_PURPLE + arenaLoadingBunker.getAuthor() + ChatColor.GOLD
                + " was successfully loaded.");
        player.sendTitle(ChatColor.GOLD + "Entered " + ChatColor.DARK_PURPLE + arenaLoadingBunker.getName(),
                ChatColor.GRAY + "Created By: " + ChatColor.DARK_PURPLE + arenaLoadingBunker.getAuthor());
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Simply do /arena join to select your own team.");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
    }

}
