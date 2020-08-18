package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class SayCommand implements CommandExecutor {
    @Command(aliases = {"!say", ".say"}, usage = "!say <target> <words>", description = "Say stuff")
    public void onSay(TextChannel channel, String[] args, User user, Message message, Server server) {
        if (RoleUtil.isStaff(user, server)) {
            if (!message.getMentionedChannels().isEmpty()) {
                message.getMentionedChannels().get(0).sendMessage(String.join(" ", args).substring(args[0].length()));
            } else {
                channel.sendMessage(String.join(" ", args));
            }
        } else message.addReaction("\uD83D\uDEAB");
    }

    @Command(aliases = {"!dm", ".dm", "!msg", ".msg"}, usage = "!dm <target> <words>", description = "Say stuff to someone")
    public void onDm(TextChannel channel, String[] args, User user, Message message, Server server) {
        if (RoleUtil.isStaff(user, server)) {
            if (!message.getMentionedUsers().isEmpty()) {
                message.getMentionedUsers().get(0).sendMessage(String.join(" ", args).substring(args[0].length()));
            } else channel.sendMessage("Must specify a user!");
        } else message.addReaction("\uD83D\uDEAB");
    }
}
