package moe.kayla.bunkerutils;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;

import java.util.List;

/**
 * @Author Kayla
 * BunkerConfiguration Class File
 */
public class BunkerConfiguration {
    private Configuration config;
    private DatabaseCredentials sqlCreds;

    private String defenderGroup;
    private String attackerGroup;

    private List<String> compactLore;

    private int arenaTagTime;

    private boolean discord;

    private boolean disableAdvancements;

    private boolean voteRestart;

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
            sqlCreds = new DatabaseCredentials(user, pass, host, port, "mysql", db, 5, 5000L, 600000L, 7200000L);

            ConfigurationSection teamConf = config.getConfigurationSection("teams");
            defenderGroup = teamConf.getString("defender");
            attackerGroup = teamConf.getString("attacker");

            compactLore = config.getStringList("compactlore");

            arenaTagTime = config.getInt("arenaTagTime");
            discord = config.getBoolean("discord");
            disableAdvancements = config.getBoolean("disableAdvancements");
            voteRestart = config.getBoolean("voteRestart");
        } catch (Exception e) {
            BunkerUtils.INSTANCE.getLogger().severe("Failed to parse configuration.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public DatabaseCredentials getSqlCreds() { return sqlCreds; }

    public String getDefenderGroup() { return defenderGroup; }

    public String getAttackerGroup() { return attackerGroup; }

    public List<String> getCompactLore() { return compactLore; }

    public int getArenaTagTime() { return arenaTagTime; }

    public boolean getDiscordEnabled() { return discord; }

    public boolean getAdvancementsDisabled() { return disableAdvancements; }

    public boolean getVoteRestart() { return voteRestart; }

}
