package moe.kayla.bunkerutils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import isaac.bastion.Bastion;
import moe.kayla.bunkerutils.command.ActiveCommand;
import moe.kayla.bunkerutils.command.SaveCommand;
import moe.kayla.bunkerutils.command.SpawnCommand;
import moe.kayla.bunkerutils.model.ArenaManager;
import moe.kayla.bunkerutils.model.BunkerDAO;
import moe.kayla.bunkerutils.model.BunkerManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.NameLayerPlugin;


import java.util.logging.Logger;

/**
 * @Author Kayla
 * BunkerUtils Plugin Class File
 */
public final class BunkerUtils extends ACivMod {
    public Logger logger = this.getLogger();

    /**
     * Internal Managers & Classes
     */
    public static BunkerUtils INSTANCE;
    public BunkerConfiguration bunkerConfiguration;
    public BunkerDAO bunkerDAO;
    public BunkerManager bunkerManager;
    public ArenaManager arenaManager;

    /**
     * External Plugin Objects
     */
    public MultiverseCore mvCore;
    public Citadel citadel;
    public Bastion bastion;

    @Override
    public void onEnable() {
        if(getConfig().getInt("version") != 1 || !getConfig().isInt("version")) {
            saveDefaultConfig();
        }
        INSTANCE = this;
        // Plugin startup logic
        logger.info("Starting BunkerUtils initialization.");
        logger.info("Created by Kayla, (github.com/KaloHG).");
        if(!Bukkit.getPluginManager().isPluginEnabled("NameLayer")
                || !Bukkit.getPluginManager().isPluginEnabled("Citadel")
                || !Bukkit.getPluginManager().isPluginEnabled("CivModCore")
                || !Bukkit.getPluginManager().isPluginEnabled("Bastion")
                || !Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")
                || !Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            logger.severe("Cannot start BunkerUtils, important dependencies missing! (Citadel/NameLayer/CivModCore/Multiverse-Core/Bastion/WorldEdit)");
            logger.severe("Stopping Plugin Initialization...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        bunkerConfiguration = new BunkerConfiguration(this.getConfig());
        if(!bunkerConfiguration.parseCfg()) {
            logger.severe("Failed to parse BunkerUtils Configuration. Stopping plugin initialization!");
            //Saving config in-case not exists.
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        bunkerManager = new BunkerManager();
        logger.info("BunkerManager initialized.");

        arenaManager = new ArenaManager();
        logger.info("ArenaManager initialized.");

        mvCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        logger.info("Established Multiverse-Core link. Protocol-Version: " + mvCore.getProtocolVersion());

        citadel = Citadel.getInstance();
        logger.info("Established Citadel Link.");

        bastion = Bastion.getPlugin();
        logger.info("Established Bastion Link.");

        logger.info("Starting MYSQL Link");
        try {
            bunkerDAO = new BunkerDAO(this, bunkerConfiguration.getSqlCreds());
        } catch (Exception e) {
            logger.severe("Failed to initialize MySQL, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        bunkerDAO.loadBunkerList();
        logger.info("Loaded " + bunkerManager.getBunkers().size() + " bunkers.");
        this.getCommand("bctworld").setExecutor(new SaveCommand());
        this.getCommand("bactive").setExecutor(new ActiveCommand());
        this.getCommand("setctspawn").setExecutor(new SpawnCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public BunkerDAO getBunkerDAO() {
        return bunkerDAO;
    }

    /**
     * Plugin Dependencies
     */
    public MultiverseCore getMvCore() {
        return mvCore;
    }

    public Citadel getCitadel() {
        return citadel;
    }

    public BunkerManager getBunkerManager() {
        return bunkerManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BunkerConfiguration getBunkerConfiguration() {
        return bunkerConfiguration;
    }
}
