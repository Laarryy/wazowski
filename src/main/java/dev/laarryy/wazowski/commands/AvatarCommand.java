package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.Constants;
import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;

public class AvatarCommand implements CommandExecutor {
    private final DiscordApi api;
    private final TextChannel modChannel;

    public AvatarCommand(DiscordApi api) {
        modChannel = api.getTextChannelById(Constants.CHANNEL_MODLOG).orElse(null);
        if (modChannel == null) {
            throw new IllegalStateException("Mod channel does not exist.");
        }
        this.api = api;
    }

    @Command(aliases = {"!avatar", ".avatar"}, usage = "!avatar <User>", description = "Shows the users' avatar")
    public void onCommand(User commandSender, String[] args, TextChannel channel, Message message, Server server) {
        if (RoleUtil.isStaff(commandSender, server) && args.length >= 1) {
            if (!message.getMentionedUsers().isEmpty()) {
                channel.sendMessage(new EmbedBuilder().setImage(message.getMentionedUsers().get(0).getAvatar()));
                return;
            }
            for (User user : server.getMembers()) {
                if (user.getName().equalsIgnoreCase(args[0])) {
                    channel.sendMessage(new EmbedBuilder().setImage(user.getAvatar()));
                    return;
                }
            }
        } else message.addReaction("\uD83D\uDEAB");
    }

    @Command(aliases = {"!setavatar"}, usage = "!setavatar <img>", description = "Sets the bot's avatar")
    public void onSetAvatar(DiscordApi api, String[] args, TextChannel channel, Message message, User user, Server server) {
        if (!message.getAuthor().asUser().isPresent()) {
            return;
        }

        if (args.length >= 1 && RoleUtil.isStaff(user, server)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Bot Avatar Changed");
            embed.setColor(Color.YELLOW);
            embed.setThumbnail("https://i.imgur.com/2Hbdxuz.png");
            embed.addInlineField("Bot Avatar Changed By", message.getAuthor().asUser().get().getMentionTag());
            embed.addField("ID", user.getIdAsString());
            embed.setFooter("Done by "+ user.getName());
            embed.setTimestamp(Instant.now());
            modChannel.sendMessage(embed);
            try {
                URL url = new URL(args[0]); //pray to god its a URL
                message.delete();
                api.createAccountUpdater().setAvatar(url).update();
                channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle("Avatar set!").setImage(args[0]));
            } catch (IOException ignored) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Unable to set avatar").setDescription("`!setavatar <url>`"));
            }
        } else message.addReaction("\uD83D\uDEAB");
    }
}
