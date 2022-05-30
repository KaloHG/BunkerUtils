package moe.kayla.bunkerutils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldedit.WorldEdit;
import isaac.bastion.Bastion;
import me.joansiitoh.lunarparty.sLunarAPI;
import moe.kayla.bunkerutils.command.*;
import moe.kayla.bunkerutils.config.ConfigurationService;
import moe.kayla.bunkerutils.gui.CreateGui;
import moe.kayla.bunkerutils.gui.JoinGui;
import moe.kayla.bunkerutils.listener.*;
import moe.kayla.bunkerutils.model.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.civmodcore.ACivMod;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    public BunkerDAO bunkerDAO;
    public BunkerManager bunkerManager;
    public ArenaManager arenaManager;
    public ConfigurationService configurationHandler;

    /**
     * Internal GUI Classes
     */
    public CreateGui createGui = new CreateGui();
    public JoinGui joinGui = new JoinGui();

    /**
     * External Plugin Objects
     */
    public sLunarAPI sLunarAPI;
    public MultiverseCore mvCore;
    public Citadel citadel;
    public Bastion bastion;
    public WorldEdit worldEdit;

    /**
     * Global Booleans
     */
    //public boolean discordEnabled;
    public boolean worldGuardEnabled;

    /**
     * Vote Handling, So small it doesn't need to be in a seperate class.
     */
    public List<UUID> votes;

    @Override
    public void onEnable() {
        INSTANCE = this;
        ArenaTickListener.creationTickListener();
        ArenaTickListener.cleanUpTask();
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

        if(!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            logger.info("WorldGuard is not loaded, disabling functionality.");
            worldGuardEnabled = false;
        } else {
            logger.info("WorldGuard is loaded, enabling functionality.");
            worldGuardEnabled = true;
        }

        ConfigurationService.init(this);


        /*
         * Cursed Discord Loading Setup
         * Basically checks our config then attempts a hook. If it fails we don't do shit.
         */
        /**
         *       if(bunkerConfiguration.getDiscordEnabled()) {
         *             logger.info("Discord Functionality is enabled, trying SRV Hook...");
         *             if(!Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
         *                 logger.warning("DiscordSRV isn't on server, but enabled in config? Check your plugins folder.");
         *                 discordEnabled = false;
         *             } else {
         *                 logger.info("Starting DiscordSRV Hook...");
         *                 discordEnabled = true;
         *                 try {
         *                     //Schedule after 15 seconds.
         *                     Bukkit.getScheduler().runTaskLater(this , () -> {
         *                         DiscordSRV.getPlugin().getConsoleChannel().sendMessage("DiscordSRV is now hooked into **BunkerUtils**.").queue();
         *                     }, 15000L);
         *                     DiscordSRV.api.subscribe(new DSRVListener());
         *                 } catch(Exception e) {
         *                     logger.severe("DiscordSRV Failed to fire a logging message. Check stack! (DISABLING DISCORD FUNCTIONALITY)");
         *                     e.printStackTrace();
         *                     discordEnabled = false;
         *                 }
         *             }
         *         }
         */

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

        sLunarAPI = new sLunarAPI(this);



        logger.info("Starting MYSQL Link");
        try {
            bunkerDAO = new BunkerDAO(this, ConfigurationService.SQLCREDS);
        } catch (Exception e) {
            logger.severe(e.getMessage());
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
        this.registerListener(new KitListener());

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
        this.getCommand("voterestart").setExecutor(new VoteRestartCommand());
        this.getCommand("bctrm").setExecutor(new AreaReinRemoveCommand());
        this.getCommand("bctremovearena").setExecutor(new RemoveArenaCommand());

        if(ConfigurationService.VOTERESTART) {
            votes = new ArrayList<>();
        }

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
        cleanUpWorlds();
    }

    @Override
    public void onDisable() {
        disableClosure();
    }

    /**
     * Disables and unloads all arena worlds.
     * Note: The deletion function doesn't delete the file. It removes it from Bukkit & Multiverse-Core
     */


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
    public void cleanUpWorlds(){
        logger.info("Cleaning up worlds folder...");
        for (File f : Bukkit.getWorldContainer().listFiles()){
            String s = f.getName();
            if((Character.isDigit(s.length() -3) && (Character.isDigit(s.length() -2)) && s.endsWith("a"))){
                logger.info("Arena folder " + s + " found, deleting...");
                f.delete();
            }
        }
    }
    private void disableClosure(){
        logger.info("Closing arena before plugin disable...");
        if(arenaManager.activeArenaWorlds().size() > 1){
            logger.info("Active arena found, closing it now...");
            for(Arena a : arenaManager.getArenas()){
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

    /**
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

     *
     *
     */

    /**
     * Vote only lasts 5 minutes, then removes.
     * @param p - player for vote to be registered.
     * @return - whether or not the player has already voted or not.
     */
    public boolean addUserVote(Player p) {
        if(votes.contains(p.getUniqueId())) {
            return false;
        }
        votes.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(this, () -> votes.remove(p.getUniqueId()), 6000L);
        return true;
    }

    public void checkIfEnoughVotes() {
        if (votes.size() > (Bukkit.getOnlinePlayers().size() / 2)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Vote threshold reached for restart. The server will restart in " + ChatColor.AQUA + "30 seconds" + ChatColor.GOLD + ".");
            Bukkit.getScheduler().runTaskLater(this, () -> { Bukkit.spigot().restart(); }, 600L);
        }
    }

    public ConfigurationService getConfigurationHandler() {
        return configurationHandler;
    }

    public BunkerDAO getBunkerDAO() {
        return bunkerDAO;
    }

    /**
     * Plugin Dependencies
     */
    public sLunarAPI getsLunarAPI(){
        return sLunarAPI;
    }

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

    public CreateGui getCreateGui() { return createGui; }

    public JoinGui getJoinGui() { return joinGui; }
}
