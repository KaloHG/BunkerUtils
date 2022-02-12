package moe.kayla.bunkerutils.model.discord;

import github.scarsz.discordsrv.DiscordSRV;
import moe.kayla.bunkerutils.model.Arena;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.Color;

public class EmbedInitializer {

    public static EmbedBuilder getArenaEmbed(Arena a) {
        //Only fired on arena creation so its not unsafe to call a player by name for an online object.
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Bunker Utilities")
                .setImage(DiscordSRV.getAvatarUrl(Bukkit.getPlayer(a.getHost())))
                .setColor(new Color(0xdd0000))
                .setTitle(a.getHost() + " has created an arena!")
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
}
