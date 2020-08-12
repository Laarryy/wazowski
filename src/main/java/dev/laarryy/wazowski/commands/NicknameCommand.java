package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.Constants;
import dev.laarryy.wazowski.util.RoleUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;

public class NicknameCommand implements CommandExecutor {

    @Command(aliases = {"!setnick", ".setnick"}, usage = "!setnick <name>", description = "Sets the nickname of the bot")
    public void onCommand(DiscordApi api, String[] args, User user, Server server) {
        if (RoleUtil.isStaff(user, server)) {
            if (args.length == 1) {
                api.getYourself().updateNickname(server, args[0]);
            } else {
                api.getYourself().updateNickname(server, api.getYourself().getName());
            }
        }
    }
}


