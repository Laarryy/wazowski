package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.stream.Collectors;

public class RoleCheckCommand implements CommandExecutor {
    @Command(aliases = {"!rolecheck", ".rolecheck"}, usage = "!rolecheck <User>", description = "Checks users' role")
    public void onCommand(TextChannel channel, String[] args, Message message, Server server, User user) {
        if (message.getUserAuthor().isPresent()) {
            if (args.length >= 1 && RoleUtil.isStaff(user, server)) {
                String string = "User Roles```";
                if (message.getAuthor().canKickUsersFromServer() || !message.getMentionedUsers().isEmpty()) {
                    for (User mentionedUser : message.getMentionedUsers()) {
                        channel.sendMessage("User Roles for " + mentionedUser.getName() + String.format("```%s```", mentionedUser.getRoles(server).stream().map(Role::getName).collect(Collectors.joining(", "))));
                    }
                    return;
                }
                for (User mentionedUser : server.getMembers()) {
                    if (RoleUtil.isStaff(user, server) || user.getName().equalsIgnoreCase(args[0])) {
                        channel.sendMessage("User Roles for " + mentionedUser.getName() + String.format("```%s```", mentionedUser.getRoles(server).stream().map(Role::getName).collect(Collectors.joining(", "))));
                        break;
                    }
                }
            } else message.addReaction("\uD83D\uDEAB");
        }
    }
}

