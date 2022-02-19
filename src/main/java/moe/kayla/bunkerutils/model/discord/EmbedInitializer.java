package moe.kayla.bunkerutils.model.discord;

import com.devotedmc.ExilePearl.ExilePearl;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import isaac.bastion.event.BastionDestroyedEvent;
import moe.kayla.bunkerutils.model.Arena;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.Color;

public class EmbedInitializer {

    public static EmbedBuilder getArenaCreationEmbed(Arena a) {
        //Only fired on arena creation so its not unsafe to call a player by name for an online object.
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(a.getHost() + " has created an arena!", null, DiscordSRV.getAvatarUrl(Bukkit.getPlayer(a.getHost())))
                .setColor(new Color(0xebb65a))
                .setDescription("On the map " + a.getBunker().getName() + ".");
        return eb;
    }

    public static EmbedBuilder getArenaClosureEmbed(Arena a) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(a.getHost() + "'s arena has closed.", null, DiscordSRV.getAvatarUrl(a.getHost(), Bukkit.getOfflinePlayer(a.getHost()).getUniqueId()))
                .setColor(new Color(0xebb65a))
                .setDescription("On the map " + a.getBunker().getName() + ".");
        return eb;
    }

    public static EmbedBuilder getVerifEmbed(User user, OfflinePlayer player) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Bunker Utilities")
                .setImage(DiscordSRV.getAvatarUrl(player.getName(), player.getUniqueId()))
                .setColor(new Color(0x3fe806))
                .setTitle(player.getName() + " has linked their account.")
                .setDescription("Linked to Discord User: " + user.getName() + "#" + user.getDiscriminator() + " D/ID: " + user.getId());
        return eb;
    }

    public static EmbedBuilder getPearledEmbed(ExilePearl p) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(p.getPlayerName() + " was pearled by " + p.getKillerName(), null, DiscordSRV.getAvatarUrl(p.getPlayerName(), p.getPlayerId()))
                .setColor(new Color(0x8d166c));
        return eb;
    }

    public static EmbedBuilder getBastionBreakEvent(Arena a, BastionDestroyedEvent event) {
        String group = event.getBastion().getGroup().getName();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(0xebb65a))
                .setTitle("A " + group + " Bastion was Broken!")
                .setDescription("On " + a.getBunker().getName() + " hosted by " + a.getHost() + ".");
        return eb;
    }
}
