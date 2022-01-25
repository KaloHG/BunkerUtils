package moe.kayla.bunkerutils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldedit.WorldEdit;
import isaac.bastion.Bastion;
import moe.kayla.bunkerutils.command.*;
import moe.kayla.bunkerutils.gui.CreateGui;
import moe.kayla.bunkerutils.gui.JoinGui;
import moe.kayla.bunkerutils.listener.CitadelListener;
import moe.kayla.bunkerutils.model.ArenaManager;
import moe.kayla.bunkerutils.model.BunkerDAO;
import moe.kayla.bunkerutils.model.BunkerManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.checkerframework.checker.units.qual.C;
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
     * Internal GUI Classes
     */
    public CreateGui createGui = new CreateGui();
    public JoinGui joinGui = new JoinGui();

    /**
     * External Plugin Objects
     */
    public MultiverseCore mvCore;
    public Citadel citadel;
    public Bastion bastion;
    public WorldEdit worldEdit;

    @Override
    public void onEnable() {
        saveConfig();
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

        worldEdit = WorldEdit.getInstance();
        logger.info("Established WorldEdit Link.");

        logger.info("Starting MYSQL Link");
        try {
            bunkerDAO = new BunkerDAO(this, bunkerConfiguration.getSqlCreds());
        } catch (Exception e) {
            logger.severe("Failed to initialize MySQL, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        bunkerDAO.loadBunkerList();
        logger.info("Loaded " + bunkerManager.getBunkers().size() + " bunkers.");
        logger.info("Loaded Citadel Listener");
        this.registerListener(new CitadelListener());
        this.getCommand("bctworld").setExecutor(new SaveCommand());
        this.getCommand("bactive").setExecutor(new ActiveCommand());
        this.getCommand("setctspawn").setExecutor(new SpawnCommand());
        this.getCommand("arena").setExecutor(new ArenaCommand());
        this.getCommand("bctar").setExecutor(new AreaReinCommand());
        this.getCommand("bb").setExecutor(new BastionizeCommand());
        this.getCommand("bctars").setExecutor(new AreaSpecificCommand());
        this.getCommand("compact").setExecutor(new CompactCommand());
        this.getCommand("blist").setExecutor(new BunkerListCommand());
        this.getCommand("bsetbeacon").setExecutor(new BeaconSetCommand());
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

    public WorldEdit getWorldEdit() { return worldEdit; }

    public BunkerManager getBunkerManager() {
        return bunkerManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BunkerConfiguration getBunkerConfiguration() {
        return bunkerConfiguration;
    }

    public CreateGui getCreateGui() { return createGui; }

    public JoinGui getJoinGui() { return joinGui; }
}
