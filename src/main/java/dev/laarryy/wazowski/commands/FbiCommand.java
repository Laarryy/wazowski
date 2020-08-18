package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import dev.laarryy.wazowski.Constants;
import org.slf4j.Logger;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class FbiCommand implements CommandExecutor {
    // This command will create and send an anti-piracy embed, and
    // add the role ROLE_PIRATE to any users mentioned, unless they
    // are zml or have permission to kick users from the server.

    private static final Logger logger = getLogger(FbiCommand.class);

    @Command(aliases = {"!fbi", ".fbi", "!piracy", ".piracy", ".pirate", "!pirate"}, usage = "!fbi", description = "Special message for thieves and their friends")
    public void onCommand(String[] args, TextChannel channel, User user, Server server, Message message) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("PIRACY = BAD");
        embed.setUrl("https://admincraft.host/fbi");
        embed.setColor(Color.RED);
        embed.setThumbnail("https://i.imgur.com/gu514NL.png");

        embed.addInlineField("Offline Mode? ", "Running an offline server allows people who have stolen the game to play it. Not only that, but it opens the door for a vast number of security threats that are best avoided.");
        embed.addInlineField("Stolen Plugins?", "Stealing the work of developers who ask for payment in exchange for their plugins is just plain wrong. If you have done this, you are not entitled to any assistance whatsoever.");
        embed.addField("What to do?", "Set `online-mode` to true in the server.properties file, or the bungeecord config if you run a network.");

        embed.setFooter("A Message for a Pirate");
        embed.setTimestamp(Instant.now());
        channel.sendMessage(embed);

        if (args.length >= 1) {
            message.getMentionedUsers();
            Optional<Role> pirate = server.getRoleById(Constants.ROLE_PIRATE);
            if (!RoleUtil.isStaff(user, server) || !RoleUtil.isLegit(user, server)) {
                message.addReaction("\uD83D\uDEAB");
                return;
            }
            if (!pirate.isPresent()) {
                logger.error("Could not find pirate role");
                message.addReaction("\uD83D\uDEAB");
                return;
            }
            for (User mentionedUser : message.getMentionedUsers()) {
                if (RoleUtil.isStaff(mentionedUser, server) || RoleUtil.isLegit(mentionedUser, server)) {
                    continue;
                }
                mentionedUser.addRole(pirate.get());
                message.addReaction("\uD83C\uDFF4\u200D☠️");
            }
        }
    }
}