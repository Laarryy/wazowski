package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import dev.laarryy.wazowski.Constants;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PresenceCommand implements CommandExecutor {
    private DiscordApi api;

    @Command(aliases = {"!presence", ".presence"}, usage = "!presence <status>", description = "Sets the status presence of the bot")
    public void onCommand(DiscordApi api, String[] args, TextChannel channel, User user, Server server, MessageAuthor messageAuthor, Message message) {
        TextChannel modChannel = api.getTextChannelById(Constants.CHANNEL_MODLOG).orElse(null);
        if (modChannel == null) {
            throw new IllegalStateException("Mod channel does not exist.");
        }

        this.api = api;

        if (RoleUtil.isStaff(user, server) && args.length >= 2) {
            String type = args[0].toUpperCase();
            String url = args[args.length-1];
            LinkedList<String> status = new LinkedList<>(Arrays.asList(args));
            status.removeFirst();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Bot Status Changed");
            embed.setColor(new Color(0x06EE27));
            embed.setThumbnail("https://i.imgur.com/KXWqFyF.png");
            embed.addInlineField("Bot Status Changed By", user.getMentionTag());
            embed.addField("Their ID", messageAuthor.getIdAsString());
            embed.setFooter("Done by " + messageAuthor.getName());
            embed.setTimestamp(Instant.now());
            modChannel.sendMessage(embed);

            try {
                if (type.equalsIgnoreCase("streaming")) {
                    status.removeLast();
                    api.updateActivity(String.join(" ", status), url);
                } else {
                    api.updateActivity(ActivityType.valueOf(type), String.join(" ", status));
                }
            } catch (Exception ignored) { // TODO: Remove unnecessary exception block
                channel.sendMessage("Invalid activity use: " + Arrays.toString(ActivityType.values()));
            }
        } else message.addReaction("\uD83D\uDEAB");
    }
}