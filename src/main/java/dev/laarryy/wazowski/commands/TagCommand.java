package dev.laarryy.wazowski.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.util.RoleUtil;
import org.apache.commons.lang.ArrayUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import dev.laarryy.wazowski.util.EmbedUtil;
import dev.laarryy.wazowski.util.KeywordsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class TagCommand implements CommandExecutor, MessageCreateListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Factoid> tagMap = new HashMap<>();
    EmbedUtil util = new EmbedUtil();
    private final ObjectMapper mapper = new ObjectMapper();

    public TagCommand(DiscordApi api) {
        api.addListener(this);
        try {
            JsonNode jsonTags = mapper.readTree(new File("./factoids.json")).get("tags");
            for (int i = 0; i < jsonTags.size(); i++) {
                Factoid userTag = mapper.readValue(jsonTags.get(i).toString(), Factoid.class);
                tagMap.put(userTag.name, userTag);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Command(aliases = {"!tagraw", "?tagraw"}, usage = "!tagraw <name>", description = "Send the raw tag message to the channel.")
    public void onTagRaw(Message message, TextChannel channel, String[] args, User user, Server server) {
        if (args.length >= 1 && tagMap.containsKey(args[0].toLowerCase()) && RoleUtil.isStaff(user, server)) {
            Factoid userTag = tagMap.get(args[0].toLowerCase());
            channel.sendMessage(user.getMentionTag(), new EmbedBuilder().setTitle(args[0] + " info").addInlineField("Created by", String.format("<@%s>", userTag.owner)).addInlineField("Modified on", Date.from(Instant.parse(userTag.modified)).toString()).addField("Content",String.format("```%s```", userTag.content)));
        } else { message.addReaction("\uD83D\uDEAB"); }
    }

    @Command(aliases = {"!tagfile", "?tagfile"}, usage = "!tagfile", description = "Gets the tag file")
    public void onTagFile(TextChannel channel, User user, Server server, Message message) {
        if (RoleUtil.isStaff(user, server)) {
            channel.sendMessage(new File("./factoids.json"));
        } message.addReaction("\uD83D\uDEAB");
    }

    @Command(aliases = {"!tags", "?tags"}, usage = "!tags [filter]", description = "List all currently enabled tags.")
    public void onList(DiscordApi api, TextChannel channel, Server server, User user, Message message) {
        if (!RoleUtil.isStaff(user, server) || !RoleUtil.isLegit(user, server)) {
            message.addReaction("\uD83D\uDEAB");
            return;
        }
        StringBuilder s = new StringBuilder();
        ArrayList<String> tagsList = new ArrayList<>(tagMap.keySet());
        Collections.sort(tagsList);
        for (String key : tagsList) {
            s.append(String.format("%s  ", key));
        }
        channel.sendMessage(new EmbedBuilder().setTitle("Active Tags").setDescription(String.format("```%s```", s.toString())).setColor(Color.GREEN));
    }

    @Command(aliases = {"!tagset", "?tagset"}, usage = "!tagset <name> [message]", description = "Set a new tag")
    public void onSet(Message message, TextChannel channel, String[] args, User user, Server server) {
        if (args.length >= 2 && RoleUtil.isStaff(user, server)) {
            String key = args[0].toLowerCase();
            if (key.contains("\\n")) {
                channel.sendMessage("How did you even think to try using that series of characters in a message? Regardless, I will simply not allow it!");
                return;
            }
            StringJoiner sb = new StringJoiner(" ");
            for(int i = 1; i < args.length; i++) {
                sb.add(args[i]);
            }
            Factoid newTag = new Factoid().setName(key).setContent(sb.toString()).setOwner(user.getIdAsString());
            tagMap.put(key, newTag);
            saveTags();
            channel.sendMessage(new EmbedBuilder().setTitle("Tag set!").setColor(Color.GREEN));
        } else { message.addReaction("\uD83D\uDEAB"); }
    }

    @Command(aliases = {"!tagunset", "?tagunset"}, usage = "!tagunset <name> [message]", description = "Unset a tag")
    public void onUnset(Message message, TextChannel channel, String[] args, User user, Server server) {
        if (args.length >= 1 && RoleUtil.isStaff(user, server)) {
            tagMap.remove(args[0].toLowerCase());
            saveTags();
            channel.sendMessage(new EmbedBuilder().setTitle("Tag removed!").setColor(Color.GREEN));
        } else { message.addReaction("\uD83D\uDEAB"); }
    }

    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        String message = ev.getMessage().getContent();
        if ((message.startsWith("?") || message.startsWith("!") || message.startsWith(".")) && message.length() >= 2) {
            String[] args = message.split(" ");
            String tag = args[0].substring(1).toLowerCase();
            args = (String[]) ArrayUtils.remove(args, 0);
            if (tagMap.containsKey(tag)) {
                String factoid = getFactoid(tag);
                if (factoid.startsWith("<staff>")) {
                    factoid = factoid.substring(7);
                    if (!ev.getMessageAuthor().canKickUsersFromServer()) return;
                }
                if (factoid.startsWith("<del>")) {
                    ev.getMessage().delete();
                    factoid = factoid.substring(5);
                }
                if (ev.getMessage().getUserAuthor().isPresent() && ev.getServer().isPresent()) {
                    if (factoid.startsWith("<embed>") || factoid.startsWith("<json>")) {
                        ev.getChannel().sendMessage(util.parseString(factoid, ev.getMessage().getUserAuthor().get(), ev.getServer().get(), args));
                    } else {
                        ev.getChannel().sendMessage(new KeywordsUtil(factoid, ev.getMessage().getUserAuthor().get(), ev.getServer().get(), args).replace());
                    }
                }
            }
        }
    }

    public String getFactoid(String tag) {
        String taag = tagMap.get(tag.toLowerCase()).content;
        return taag.startsWith("?") ? tagMap.get(taag.split(" ")[0].substring(1).toLowerCase()).content : taag;
    }

    public void saveTags() {
        try {
            mapper.writerWithDefaultPrettyPrinter().withRootName("tags").writeValue(new File("./factoids.json"), tagMap.values());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    static class Factoid {
        public String name;
        public String content;
        public String owner;
        public String modified;

        public Factoid setName(String name) {
            this.name = name;
            return this;
        }

        public Factoid setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Factoid setContent(String content) {
            this.content = content;
            return this;
        }

        Factoid() {
            this.owner = "Servercraft";
            this.name = "";
            this.content = "";
            this.modified = Instant.now().toString();
        }
    }
}

