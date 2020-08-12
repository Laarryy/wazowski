package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.Constants;
import dev.laarryy.wazowski.storage.RolePollStorage;
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

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoleReactionCommand implements CommandExecutor, ReactionAddListener, ReactionRemoveListener {

    RolePollStorage storage = new RolePollStorage();
    Map<String, String> roleMap = new LinkedHashMap<>();

    public RoleReactionCommand(DiscordApi api) {
        api.addListener(this);
        roleMap.put("\uD83D\uDCAD", Constants.ROLE_CHAT_UPDATES);
        /*roleMap.put("\uD83D\uDC68", Constants.ROLE_PROFESSIONS_UPDATES);
        roleMap.put("\uD83D\uDC7D", Constants.ROLE_SYSOUT_UPDATES);
        roleMap.put("\uD83D\uDEEB", Constants.ROLE_TELEPORTS_UPDATES);
        roleMap.put("\uD83E\uDDCA", Constants.ROLE_COOLDOWNS_UPDATES);
        roleMap.put("\uD83D\uDD28", Constants.ROLE_KITS_UPDATES);
        roleMap.put("\uD83D\uDD2B", Constants.ROLE_GUNPOWDER_UPDATES);
        roleMap.put("\uD83E\uDD9C", Constants.ROLE_CHATGAMES_UPDATES);*/

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
    public void onUpdate(TextChannel channel, User user, Server server, String[] args, Message cmd) {
        if (server.canKickUsers(user)) {
            cmd.delete();
            try {
                switch (channel.getIdAsString()) {
                    case "732527807216877608": //Carbon
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_CHAT_UPDATES).get());
                        break;
                    /*case "737570032766287892": //Professions
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_PROFESSIONS_UPDATES).get());
                        break;
                    case "737570162584191057": //Sysout
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_SYSOUT_UPDATES).get());
                        break;
                    case "737570139096219668": //Teleports
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_TELEPORTS_UPDATES).get());
                        break;
                    case "737570103549362207": //Cooldowns
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_COOLDOWNS_UPDATES).get());
                        break;
                    case "737570183417430087": //Kits
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_KITS_UPDATES).get());
                        break;
                    case "737570083357851698": //Gunpowder
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_GUNPOWDER_UPDATES).get());
                        break;
                    case "737570268708339772": //Chatgames
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_CHATGAMES_UPDATES).get());
                        break;
                    case "738239418015678574": //Test
                        broadcast(String.join(" ", args), channel, server.getRoleById(Constants.ROLE_CHATGAMES_UPDATES).get());
                        break;*/
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
                "```Click the \uD83D\uDCAD to subscribe to Carbon```");
                       /* "\nClick the \uD83D\uDC68 to subscribe to Professions" +
                        "\nClick the \uD83D\uDC7D to subscribe to Sysout" +
                        "\nClick the \uD83D\uDEEB to subscribe to Teleports" +
                        "\nClick the \uD83E\uDDCA to subscribe to Cooldowns" +
                        "\nClick the \uD83D\uDD28 to subscribe to Kits" +
                        "\nClick the \uD83D\uDD2B to subscribe to Gunpowder" +
                        "\nClick the \uD83E\uDD9C to subscribe to ChatGames*/
        return embed;
    }

    public void onReactionAdd(ReactionAddEvent event) {
        if (event.getUser().isYourself() || !storage.isPoll(event.getMessageId())) {
            return;
        }
        if (!event.getReaction().isPresent()) {
            event.requestReaction().thenAccept(reaction -> {
                if (reaction.isPresent()) {
                    if (storage.isPoll(event.getMessageId()) && reaction.get().containsYou()) {
                        updateRole(event.getUser(), roleMap.get(reaction.get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "add");
                    } else {
                        event.removeReaction();
                    }
                }
            });
        } else if (storage.isPoll(event.getMessageId()) && event.getReaction().get().containsYou()) {
            updateRole(event.getUser(), roleMap.get(event.getReaction().get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "add");
        } else {
            event.removeReaction();
        }
    }

    public void onReactionRemove(ReactionRemoveEvent event) {
        if (event.getUser().isYourself()) {
            return;
        }
        if (storage.isPoll(event.getMessageId()) && event.getReaction().isPresent() && event.getReaction().get().containsYou()) {
            updateRole(event.getUser(), roleMap.get(event.getReaction().get().getEmoji().asUnicodeEmoji().get()), event.getServer().get(), "remove");
        }
    }

    public void updateRole(User user, String role, Server server, String type) {
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
