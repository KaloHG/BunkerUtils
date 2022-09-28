# BunkerUtils
A plugin that adds-on to the Citadel plugin commonly contained within the CivCraft Suite. These additions are aimed toward improving the ability to create bunker-fights.

*Thank you,* Capri for maintaining briefly and Okx for helping out with the code.

## Dependencies
All CivClassic dependencies can be found under their [AnsibleSetup](https://github.com/CivClassic/AnsibleSetup) repository.
 - [CivModCore](https://github.com/CivClassic/CivModCore)
 - [Citadel](https://github.com/CivClassic/Citadel)
 - [Bastion](https://github.com/CivClassic/Bastion)
 - [NameLayer](https://github.com/CivClassic/NameLayer)
 - [ExilePearl](https://github.com/CivClassic/ExilePearl)
 - [Multiverse-Core](https://www.spigotmc.org/resources/multiverse-core.390/)
 - [WorldEdit](https://dev.bukkit.org/projects/worldedit) (NOT FAWE)
 - [CombatTagPlus](https://github.com/CivClassic/CombatTagPlus) (Functionality Available, not required).
 
 - [MariaDB MySQL Database](https://mariadb.org/download/)
 
 ## Setup
 Compile BunkerUtils or locate a Binary (.jar) file and put it in your ./plugins folder. Permit the default config to generate, if it does not a default one
 can be found at src/main/resources/config.yml. Ensure you have decent specs for a server (2 GB Mem + 2 Cores at LEAST) and have a MariaDB MySQL Database
 loaded and on from the link above. Fill out the config.yml with the proper credentials for the MariaDB server and DB. **Ensure that Citadel & Bastion use the
 same database as BunkerUtils, if they do not Bunkers will fail to import and export!**. Start your server and it should load and run. Use /help BunkerUtils to
 become familiar with some commands.
 
 ## Command Descriptions
  - /bctworld <NAME> <DESCRIPTION>, This command performs a bunker creation on the world the player is in, copying all the reinforcements and bastion to a bunker DB.
  - /bactive <WORLD>, Only use this if /arena create does not work. This command creates a new arena with a scalability of 1.
  - /blist, Shows all bunkers that are loaded into memory.
  - /setctspawn [Defenders|Attackers], Sets the spawns for a bunker if its present for the current world you are in. Make sure to do this before trying to activate that bunker.
  - /arena, multipurpose command. Just run /arena and a help menu will display.
  - /bctar <group>, reinforces all blocks within a WorldEdit selection UNLESS they are unreinforcable or already reinforced.
  - /bctars <group> <block>, reinforces specific blocks within a WorldEdit selection, overwrites pre-existing reins.
  - /bb <group> <block> <type>, "bastionizes" WorldEdit selections based off the specific block provided and the bastion type provided.
  - /compact, turns the current held ItemStack into a compacted item.
  
Have fun with bunkies.
(Rev. 3/04/22)
