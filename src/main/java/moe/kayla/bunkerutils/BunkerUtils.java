package moe.kayla.bunkerutils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldedit.WorldEdit;
import isaac.bastion.Bastion;
import moe.kayla.bunkerutils.command.*;
import moe.kayla.bunkerutils.gui.CreateGui;
import moe.kayla.bunkerutils.gui.JoinGui;
import moe.kayla.bunkerutils.listener.*;
import moe.kayla.bunkerutils.model.ArenaManager;
import moe.kayla.bunkerutils.model.Bunker;
import moe.kayla.bunkerutils.model.BunkerDAO;
import moe.kayla.bunkerutils.model.BunkerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.civmodcore.ACivMod;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                || !Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
                || !Bukkit.getPluginManager().isPluginEnabled("ExilePearl")) {
            logger.severe("Cannot start BunkerUtils, important dependencies missing! (Citadel/NameLayer/CivModCore/Multiverse-Core/Bastion/WorldEdit/ExilePearl)");
            logger.severe("Stopping Plugin Initialization...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //CTag Check and Listener load if loaded.
        if(!Bukkit.getPluginManager().isPluginEnabled("CombatTagPlus")) {
            logger.warning("CombatTagPlus is not loaded, functionality will not be implemented. CTPlus is strongly recommended for this plugin.");
        } else {
            logger.info("Starting CTPlus listener.");
            this.registerListener(new CombatTagListener());
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
        logger.info(ChatColor.GOLD + "Loaded " + ChatColor.AQUA + bunkerManager.getBunkers().size() + ChatColor.GOLD + " bunkers.");

        /**
         * Listener Loading
         */
        this.registerListener(new CitadelListener());
        this.registerListener(new CoreListener());
        this.registerListener(new MultiverseListener());
        this.registerListener(new TeamListener());

        /**
         * Command Loading
         */
        this.getCommand("bctworld").setExecutor(new SaveCommand());
        this.getCommand("setctspawn").setExecutor(new SpawnCommand());
        this.getCommand("arena").setExecutor(new ArenaCommand());
        this.getCommand("bctar").setExecutor(new AreaReinCommand());
        this.getCommand("bb").setExecutor(new BastionizeCommand());
        this.getCommand("bctars").setExecutor(new AreaSpecificCommand());
        this.getCommand("compact").setExecutor(new CompactCommand());
        this.getCommand("blist").setExecutor(new BunkerListCommand());
        this.getCommand("bsetbeacon").setExecutor(new BeaconSetCommand());

        //Clean out old worlds, eventually schedule this to be a synchronous repeated thing that occurs.
        getLogger().info(ChatColor.GREEN + "Started ArenaWorld cleanup...");
        disableOldArenaWorlds();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void disableOldArenaWorlds() {
        List<String> bunkerWorlds = new ArrayList<>();
        //Raw bunker world names
        for(Bunker bunk : bunkerManager.getBunkers()) {
            bunkerWorlds.add(bunk.getWorld());
        }
        for(MultiverseWorld world : mvCore.getMVWorldManager().getMVWorlds()) {
            //World contains a bunker name, but isn't equal to said name meaning it was a created Arena.
            for(String bunk : bunkerWorlds) {
                if(world.getName().contains(bunk) && !world.getName().equals(bunk)) {
                    //Remove the world so it no longer takes up resources.
                    BunkerUtils.INSTANCE.getLogger().info("Unloading old ArenaWorld: " + world.getName());
                    mvCore.getMVWorldManager().deleteWorld(world.getName(), true, false);
                }
            }
        }
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
