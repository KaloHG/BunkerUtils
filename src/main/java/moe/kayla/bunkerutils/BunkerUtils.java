package moe.kayla.bunkerutils;

import com.devotedmc.ExilePearl.ExilePearl;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldedit.WorldEdit;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import isaac.bastion.Bastion;
import isaac.bastion.event.BastionDestroyedEvent;
import moe.kayla.bunkerutils.command.*;
import moe.kayla.bunkerutils.gui.CreateGui;
import moe.kayla.bunkerutils.gui.JoinGui;
import moe.kayla.bunkerutils.listener.*;
import moe.kayla.bunkerutils.model.*;
import moe.kayla.bunkerutils.model.discord.EmbedInitializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.civmodcore.ACivMod;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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

    /**
     * Global Booleans
     */
    public boolean discordEnabled;

    @Override
    public void onEnable() {
        //todo unfuck config
        saveConfig();
        if(getConfig().getInt("version") != 1 || !getConfig().isInt("version")) {
            saveDefaultConfig();
        }
        INSTANCE = this;
        // Plugin startup logic
        logger.info("Starting BunkerUtils initialization.");
        logger.info("Created by Kayla, (github.com/KaloHG).");
        /*
         * Forced Dependency Check
         */
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

        /*
         * Cursed Discord Loading Setup
         * Basically checks our config then attempts a hook. If it fails we don't do shit.
         */
        if(bunkerConfiguration.getDiscordEnabled()) {
            logger.info("Discord Functionality is enabled, trying SRV Hook...");
            if(!Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
                logger.warning("DiscordSRV isn't on server, but enabled in config? Check your plugins folder.");
                discordEnabled = false;
            } else {
                logger.info("Starting DiscordSRV Hook...");
                discordEnabled = true;
                try {
                    //Schedule after 15 seconds.
                    Bukkit.getScheduler().runTaskLater(this , () -> {
                        DiscordSRV.getPlugin().getConsoleChannel().sendMessage("DiscordSRV is now hooked into **BunkerUtils**.").queue();
                    }, 15000L);
                    DiscordSRV.api.subscribe(new DSRVListener());
                } catch(Exception e) {
                    logger.severe("DiscordSRV Failed to fire a logging message. Check stack! (DISABLING DISCORD FUNCTIONALITY)");
                    e.printStackTrace();
                    discordEnabled = false;
                }
            }
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

        /*
         * Listener Loading
         */
        this.registerListener(new CitadelListener());
        this.registerListener(new CoreListener());
        this.registerListener(new MultiverseListener());
        this.registerListener(new TeamListener());

        /*
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

        /*
         * Arena auto-closure task
         * Runs every minute.
         */
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                runArenaClosureTask();
            }
        }, 1200L, 1200L);

        /*
         * Arena player-check task.
         * Runs every minute.
         */
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                runArenaPlayerCheckTask();
            }
        }, 1200L, 1200L);

        //Clean out old worlds, eventually schedule this to be a synchronous repeated thing that occurs.
        getLogger().info(ChatColor.GREEN + "Started ArenaWorld cleanup...");
        disableOldArenaWorlds();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Disables and unloads all arena worlds.
     * Note: The deletion function doesn't delete the file. It removes it from Bukkit & Multiverse-Core
     */
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

    private void runArenaClosureTask() {
        logger.info("Starting arena closure task... searching for arena's not in-use.");
        for(Arena a : arenaManager.getArenas()) {
            boolean isActive = false;
            if(Bukkit.getOfflinePlayer(a.getHost()).isOnline()) {
                isActive = true;
            }
            for(UUID u : a.getAllPlayers()) {
                if(Bukkit.getOfflinePlayer(u).isOnline()) {
                    isActive = true;
                }
            }
            if(!isActive) {
                //No players online, close it.
                a.close();
            }
        }
    }

    private void runArenaPlayerCheckTask() {
        logger.info("Starting player arena check task... one moment.");
        for(Arena a : arenaManager.getArenas()) {
            logger.info(ChatColor.GOLD + "Starting check task for arena: " + ChatColor.AQUA + a.getHost());
            a.cleanPlayers();
        }
    }

    public void sendArenaCreationMessage(Arena a) {
        if(discordEnabled) {
            EmbedBuilder eb = EmbedInitializer.getArenaCreationEmbed(a);
            DiscordSRV.getPlugin().getMainTextChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public void sendArenaClosureMessage(Arena a) {
        if(discordEnabled) {
            EmbedBuilder eb = EmbedInitializer.getArenaClosureEmbed(a);
            DiscordSRV.getPlugin().getMainTextChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public void sendPlayerPearledMessage(ExilePearl pearl) {
        if(discordEnabled) {
            EmbedBuilder eb = EmbedInitializer.getPearledEmbed(pearl);
            DiscordSRV.getPlugin().getMainTextChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public void sendBastionBreakMessage(Arena a, BastionDestroyedEvent event) {
        if(discordEnabled) {
            EmbedBuilder eb = EmbedInitializer.getBastionBreakEvent(a, event);
            DiscordSRV.getPlugin().getMainTextChannel().sendMessageEmbeds(eb.build()).queue();
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
