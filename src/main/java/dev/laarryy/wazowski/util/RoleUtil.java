package dev.laarryy.wazowski.util;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.Role;
import dev.laarryy.wazowski.Constants;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;

public class RoleUtil {
    public static boolean hasStaffMention(Message message) {
        if (message.getServer().isEmpty()) throw new IllegalStateException("Message does not have a server");
        return message.getMentionedUsers().get(0).getRoles(message.getServer().get()).stream()
                .anyMatch(role -> role.getIdAsString().equals(Constants.ROLE_MODERATOR) || role.getIdAsString().equals(Constants.ROLE_ADMIN));
    }
    public static boolean isStaff(User user, Server server) {
        List<Role> userRoles = user.getRoles(server);
        for (Role role : userRoles) {
            String roleId = role.getIdAsString();
            if ((roleId.equals(Constants.ROLE_MODERATOR) || roleId.equals(Constants.ROLE_ADMIN))) {
                return true;
            }
        }
        return false;
    }
    public static boolean isLegit(User user, Server server) {
        List<Role> userRoles = user.getRoles(server);
        for (Role role : userRoles) {
            String roleId = role.getIdAsString();
            if ((roleId.equals(Constants.ROLE_LEGIT))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBooster(User user, Server server) {
        List<Role> userRoles = user.getRoles(server);
        for (Role role : userRoles) {
            String roleId = role.getIdAsString();
            if ((roleId.equals(Constants.ROLE_RICH) || roleId.equals(Constants.ROLE_VERYRICH))) {
                return true;
            }
        }
        return false;
    }
}

