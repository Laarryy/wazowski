package org.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;
import org.laarryy.wazowski.Constants;
import org.laarryy.wazowski.storage.RolePollStorage;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoleReactionCommand implements CommandExecutor, ReactionAddListener, ReactionRemoveListener {

    RolePollStorage storage = new RolePollStorage();
    Map<String, String> roleMap = new LinkedHashMap<>();

    public RoleReactionCommand(DiscordApi api) {
        api.addListener(this);
        roleMap.put("\uD83C\uDF4D", Constants.ROLE_CHAT_UPDATES);
        roleMap.put("\uD83C\uDF6A", Constants.ROLE_PROFESSIONS_UPDATES);
        roleMap.put("\uD83C\uDF54", Constants.ROLE_SYSOUT_UPDATES);
        roleMap.put("\ud83C\uDF2F", Constants.ROLE_TELEPORTS_UPDATES);
        roleMap.put("\uD83C\uDF7A", Constants.ROLE_COOLDOWNS_UPDATES);
        roleMap.put("\uD83D\uDDDD", Constants.ROLE_KITS_UPDATES);

    }

    @Command(aliases = {"!rolepoll", ".rolepoll"}, usage = "!rolepoll", description = "Polls users for update roles")
    public void onCommand(DiscordApi api, TextChannel channel, User user, Server server, Message cmd) {
        if (server.isAdmin(user)) {
            cmd.delete();
            try {
                Message msg = channel.sendMessage(createPoll()).get();
                roleMap.keySet().forEach(msg::addReaction);
                storage.set(msg.getIdAsString(), msg.getChannel().getIdAsString());
            } catch (Exception e) {}
        }
    }

    @Command(aliases = {"!rpupdate", ".rpupdate"}, usage = "!rpupdate", description = "Updates all roll polls.")
    public void onRPUpdate(DiscordApi api, TextChannel channel, User user, Server server, Message cmd) {
        if (server.isAdmin(user)) {
            cmd.delete();
            try {
                for (String key : storage.getMap().keySet()) {
                    api.getMessageById(key, api.getTextChannelById(storage.getChannel(key)).get()).thenAcceptAsync(msg -> {
                        msg.edit(createPoll());
                        roleMap.keySet().forEach(msg::addReaction);
                    });
                }
            } catch (Exception e) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Unable to update polls"));
            }
        }
    }

    /*
    TODO: Map the channels instead.
     */
    @Command(aliases = {"!update", ".update"}, usage = "!update", description = "Polls users for update roles")
    public void onUpdate(DiscordApi api, TextChannel channel, User user, Server server, String[] args, Message cmd) {
        if (server.canKickUsers(user)) {
            cmd.delete();
            try {
                switch (channel.getIdAsString()) {
                    case "426460619277991936": //SimpleChat
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_CHAT_UPDATES).get());
                        break;
                    case "426460663498407948": //Professions
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_PROFESSIONS_UPDATES).get());
                        break;
                    case "426460690136694795": //Sysout
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_SYSOUT_UPDATES).get());
                        break;
                    case "479919913067216897": //Teleports
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_TELEPORTS_UPDATES).get());
                        break;
                    case "430125681645453325": //Cooldowns
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_COOLDOWNS_UPDATES).get());
                        break;
                    case "632427764707753994": //Kits
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_KITS_UPDATES).get());
                        break;
                    default:
                        channel.sendMessage(user.getMentionTag(), new EmbedBuilder().setTitle("Invalid update channel").setColor(Color.RED));
                }
            } catch (Exception e) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Failed"));
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String payload, TextChannel channel, Role role) {
        try {
            role.createUpdater().setMentionableFlag(true).setAuditLogReason("Update command").update();
            channel.sendMessage(role.getMentionTag() + " " + payload);
            role.createUpdater().setMentionableFlag(false).update();
        } catch (Exception e) {
            e.printStackTrace();
            role.createUpdater().setMentionableFlag(false).update();
        }
    }

    private EmbedBuilder createPoll() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(Color.GREEN);
        embed.addField("Subscribe to plugin updates",
                "```Click the \uD83C\uDF4D to subscribe to SimpleChat" +
                        "\nClick the \uD83C\uDF6A to subscribe to Professions" +
                        "\nClick the \uD83C\uDF54 to subscribe to Sysout" +
                        "\nClick the \ud83C\uDF2F to subscribe to Teleports" +
                        "\nClick the \uD83C\uDF7A to subscribe to Cooldowns" +
                        "\nClick the \ud83d\udddd\ufe0f to subscribe to Kits```");
        return embed;
    }

    public void onReactionAdd(ReactionAddEvent event) {
        if (event.getUser().isYourself()) {
            return;
        }
        if (!event.getReaction().isPresent()) {
            event.removeReaction();
            return;
        }
        if (storage.ispoll(event.getMessageId())) {
            if (event.getReaction().get().containsYou()) {
                updateRole(event.getUser(), roleMap.get(event.getReaction().get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "add");
            } else {
                event.removeReaction();
            }
        }
    }

    public void onReactionRemove(ReactionRemoveEvent event) {
        if (event.getUser().isYourself()) {
            return;
        }
        if (storage.ispoll(event.getMessageId()) && event.getReaction().isPresent() && event.getReaction().get().containsYou()) {
            updateRole(event.getUser(), roleMap.get(event.getReaction().get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "remove");
        }
    }

    public void updateRole(User user, String role, Server server, String type) { //TODO
        Role target = server.getRoleById(role).get();
        if (user.getRoles(server).stream().anyMatch(role1 -> role1.getIdAsString().equalsIgnoreCase(role)) && type.equalsIgnoreCase("remove")) {
            user.removeRole(target, "Role Poll");
        } else {
            if (type.equalsIgnoreCase("add")) {
                user.addRole(target, "Role Poll");
            }
        }
    }

}