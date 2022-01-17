package moe.kayla.bunkerutils;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;

/**
 * @Author Kayla
 * BunkerConfiguration Class File
 */
public class BunkerConfiguration {
    private Configuration config;
    private DatabaseCredentials sqlCreds;


    public BunkerConfiguration(Configuration config) {
        this.config = config;
    }

    /**
     * Parses the config
     * @return - whether the method succeeded.
     */
    public boolean parseCfg() {
        try {
            ////5, 1000L, 600000L, 7200000L
            ConfigurationSection sqlConf = config.getConfigurationSection("mysql");
            String host = sqlConf.getString("host");
            int port = sqlConf.getInt("port");
            String db = sqlConf.getString("dbname");
            String user = sqlConf.getString("username");
            String pass = sqlConf.getString("password");
            sqlCreds = new DatabaseCredentials(user, pass, host, port, "mysql", db, 5, 1000L, 600000L, 7200000L);
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("Failed to parse configuration.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public DatabaseCredentials getSqlCreds() { return sqlCreds; }
}
