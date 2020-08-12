package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.Constants;
import dev.laarryy.wazowski.util.ChannelUtil;
import dev.laarryy.wazowski.util.EmbedUtil;
import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class EmbedCommand implements CommandExecutor {

    EmbedUtil embedUtil = new EmbedUtil();

    @Command(aliases = {"!embed", ".embed"}, usage = "!embed <url>", description = "Makes an embed from a json text")
    public void onCommand(Message command, String[] args, User user, TextChannel channel, Server server) {
        if (!ChannelUtil.isNonPublicChannel(channel) || !ChannelUtil.isOffTopic(channel)) {
            command.addReaction("\uD83D\uDEAB");
            channel.sendMessage("This command must be done in #off-topic");
            return;
        }
        if (args.length == 0) {
            channel.sendMessage(new EmbedBuilder().setTitle("Invalid URL").setColor(Color.RED));
            return;
        }
        if (RoleUtil.isStaff(user, server)) {
            channel.sendMessage(user.getMentionTag(), embedUtil.parseString(String.join(" ", args), user, server));
        } else if (ChannelUtil.isNonPublicChannel(channel) || ChannelUtil.isOffTopic(channel)) {
            channel.sendMessage(user.getMentionTag(), embedUtil.parseString(String.join(" ", args), user, server));
        } else command.addReaction("\uD83D\uDEAB");
    }

}
