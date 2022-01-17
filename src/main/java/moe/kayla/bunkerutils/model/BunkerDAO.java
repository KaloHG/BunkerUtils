package moe.kayla.bunkerutils.model;

import moe.kayla.bunkerutils.BunkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.citadel.reinforcementtypes.ReinforcementType;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CivModCorePlugin;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;
import vg.civcraft.mc.civmodcore.locations.chunkmeta.block.BlockBasedChunkMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author Kayla
 * BunkerDAO Class File
 */
public class BunkerDAO extends ManagedDatasource {

    public BunkerDAO(ACivMod plugin, DatabaseCredentials credentials) {
        super(plugin, credentials);
        prepareMigrations();
        updateDatabase();
    }

    /**
     * Initialization Tables
     */
    private void prepareMigrations() {
        registerMigration(0, false, "CREATE TABLE IF NOT EXISTS `bunker_info`(`BunkerUUID` VARCHAR(50) NOT NULL,`BunkerName` VARCHAR(50) NOT NULL,`BunkerAuthor` VARCHAR(50) NOT NULL,`BunkerDescription` TEXT NOT NULL,`BunkerWorld` VARCHAR(50) NOT NULL);");
    }

    /**
     * Saves bunkers to the SQL database
     * @return - Whether the method succeeded.
     */
    public boolean saveBunkerList() {
        try {
            Connection conn = getConnection();
            PreparedStatement bunkerSaveStatement = conn.prepareStatement("insert into bunker_info(BunkerUUID, BunkerName, BunkerAuthor, BunkerDescription, BunkerWorld)" +
                    " values (?,?,?,?,?);");
            PreparedStatement deleteBunkIfExists = conn.prepareStatement("delete from bunker_info where BunkerUUID = ?");
            for(Bunker bunker : BunkerUtils.INSTANCE.getBunkerManager().getBunkers()) {
                deleteBunkIfExists.setString(1, bunker.getUuid().toString());
                deleteBunkIfExists.execute();
                bunkerSaveStatement.setString(1, bunker.getUuid().toString());
                bunkerSaveStatement.setString(2, bunker.getName());
                bunkerSaveStatement.setString(3, bunker.getAuthor());
                bunkerSaveStatement.setString(4, bunker.getDescription());
                bunkerSaveStatement.setString(5, bunker.getWorld());
                bunkerSaveStatement.execute();
            }
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("Failed to save BunkerList.");
            return false;
        }
        return true;
    }

    /**
     * Loads bunkers from the SQL Database into memory.
     * @return - Whether the method succeeded.
     */
    public boolean loadBunkerList() {
        List<Bunker> bunkerList = new ArrayList<>();
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bunker_info;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                UUID uid = UUID.fromString(rs.getString(1));
                String name = rs.getString(2);
                String author = rs.getString(3);
                String desc = rs.getString(4);
                String world = rs.getString(5);
                Bunker newBunk = new Bunker(uid, name, world, author, desc);
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
     * @param bunker - The bunker to be saved.
     * @return - Whether the method succeeded.
     */
    public boolean createNewReinWorld(Bunker bunker) {
        /**
         * Citadel Export
         */
        try {
            Connection conn = getConnection();
            PreparedStatement prep = conn.prepareStatement("CREATE TABLE `bunker_" + bunker.getWorld() + "_reinforcements` ("
	+ "`x` INT NOT NULL,"
    +                "`y` INT NOT NULL,"
    +                "`z` INT NOT NULL,"
    +                "`material_id` INT NOT NULL,"
    +                "`durability` INT NOT NULL,"
    +                "`group_id` INT NOT NULL,"
    +                "`maturation_time` INT NOT NULL,"
    +                "`rein_type_id` INT NOT NULL);");
            prep.execute();
            int id = CivModCorePlugin.getInstance().getWorldIdManager().getInternalWorldIdByName(bunker.getWorld());
            //Forcibly Flush Citadel & Bastion Data to DB.
            CivModCorePlugin.getInstance().getChunkMetaManager().flushAll();
            PreparedStatement loadStatement = conn.prepareStatement("SELECT * FROM ctdl_reinforcements WHERE world_id = " + id +";");
            PreparedStatement insertStatement = conn.prepareStatement("insert into bunker_" + bunker.getWorld() +"_reinforcements(x, y, z, material_id, durability, group_id, maturation_time, rein_type_id) values (?,?,?,?,?,?,?,?);");
            ResultSet rs = loadStatement.executeQuery();
            while(rs.next()) {
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
                insertStatement.setInt(5,dura);
                insertStatement.setInt(6, group_id);
                insertStatement.setInt(7, maturation_time);
                insertStatement.setInt(8, rein_type_id);
                insertStatement.execute();
            }
            PreparedStatement bunkerSaveStatement = conn.prepareStatement("insert into bunker_info(BunkerUUID, BunkerName, BunkerAuthor, BunkerDescription, BunkerWorld)" +
                    " values (?,?,?,?,?);");
            bunkerSaveStatement.setString(1, bunker.getUuid().toString());
            bunkerSaveStatement.setString(2, bunker.getName());
            bunkerSaveStatement.setString(3, bunker.getAuthor());
            bunkerSaveStatement.setString(4, bunker.getDescription());
            bunkerSaveStatement.setString(5, bunker.getWorld());
            bunkerSaveStatement.execute();
        } catch (Exception ex) {
            BunkerUtils.INSTANCE.getLogger().severe("(CITADEL FAILURE) Failed to save BunkerWorld " + bunker.getWorld());
            ex.printStackTrace();
            return false;
        }
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE `bunker_" + bunker.getWorld() +"_bastions");
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("(BASTION FAILURE) Failed to save BunkerWorld " + bunker.getWorld());
            e.printStackTrace();
        }
        BunkerUtils.INSTANCE.getBunkerManager().addBunker(bunker);
        return true;
    }

    /**
     * Loads a new bunker world and imports it all into citadel.
     * @param bunker - The bunker to be loaded.
     * @return - The world name if loaded, or null if failed.
     */
    public String startReinWorld(Bunker bunker) {
        String uid = UUID.randomUUID().toString();
        String worldName = bunker.getWorld() + "_" + uid;
        BunkerUtils.INSTANCE.getMvCore().getCore().getMVWorldManager().cloneWorld(bunker.getWorld(), worldName);
        BunkerUtils.INSTANCE.getLogger().info("MultiVerse world created with name: " + worldName);
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bunker_"+bunker.getWorld()+"_reinforcements;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int x = rs.getInt(1);
                int y = rs.getInt(2);
                int z = rs.getInt(3);
                int material_id = rs.getInt(4);
                int dura = rs.getInt(5);
                int group_id = rs.getInt(6);
                int maturation_time = rs.getInt(7);
                int rein_type_id = rs.getInt(8);
                Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
                ReinforcementType reinType = BunkerUtils.INSTANCE.getCitadel().getReinforcementTypeManager().getById((short) rein_type_id);
                Reinforcement newImport = new Reinforcement(loc, reinType, group_id, maturation_time, dura, false, false);
                BunkerUtils.INSTANCE.getCitadel().getReinforcementManager().putReinforcement(newImport);
                BunkerUtils.INSTANCE.getLogger().info("Importing new rein at " + x + " " + y + " " + z);
            }
            BunkerUtils.INSTANCE.getLogger().info("Successful import of " + rs.getFetchSize() + " reinforcements into Citadel Database.");
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("Failed to create a new bunker world.");
            e.printStackTrace();
            return null;
        }
        return worldName;
    }
}
