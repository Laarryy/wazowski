package dev.laarryy.wazowski.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.util.ChannelUtil;
import dev.laarryy.wazowski.util.RoleUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class XkcdCommand implements CommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String searchURL = "https://relevantxkcd.appspot.com/process?action=xkcd&query=%s";
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    @Command(aliases = {"!xkcd", "!.xkcd"}, usage = "!xkcd <Query>", description = "Search xkcd")
    public void onCommand(Server server, User user, TextChannel channel, String[] args, Message message) {
        if (!(ChannelUtil.isOffTopic(channel) || ChannelUtil.isNonPublicChannel(channel) || RoleUtil.isStaff(user, server))) {
            message.addReaction("\uD83D\uDEAB");
            return;
        }
        if (args.length >= 1) {
            String id = search(String.join(" ", args));
            if (id != null) {
                JsonNode node = xkcdInfo(id);
                if (node == null) {
                    return;
                }
                EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW);
                String link = node.get("link").asText();
                embed.setTitle("xkcd: " + node.get("safe_title").asText());
                embed.setUrl(link.equalsIgnoreCase("") ? String.format("https://xkcd.com/%s/", id) : link);
                embed.setImage(node.get("img").asText());
                embed.setFooter(node.get("alt").asText());
                channel.sendMessage(user.getMentionTag(), embed);
            }
        }
    }

    private String search(String query) {
        try {
            Request request = new Request.Builder().url(String.format(searchURL, query)).build();
            String response = Objects.requireNonNull(client.newCall(request).execute().body()).string().split(" ")[2].replaceAll("\n","");//.replaceAll("\\D+"," ")
            return StringUtils.isNumeric(response) ? response : null;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    private JsonNode xkcdInfo(String comicNum)  {
        try {
            Request request = new Request.Builder()
                    .url(String.format("https://xkcd.com/%s/info.0.json", comicNum))
                    .build();
            return mapper.readTree(Objects.requireNonNull(client.newCall(request).execute().body()).string());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}