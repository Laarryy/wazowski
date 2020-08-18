package dev.laarryy.wazowski.commands;

import com.fasterxml.jackson.databind.JsonNode;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.Constants;
import dev.laarryy.wazowski.util.BStatsUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Iterator;

public class SpigetCommand implements CommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    String queryurl = "https://api.spiget.org/v2/search/resources/{0}?field=name&size=5&page={1}&fields=id,name,tag,rating";

    @Command(aliases = {"!spiget", "!plsearch"}, usage = "!spiget <Query>", description = "Search spigots resources")
    public void onCommand(DiscordApi api, TextChannel channel, String[] args, Message command) {
        String query = String.join(" ", args);
        String lastIndex = query.substring(query.lastIndexOf(" ")+1);
        int page = 1;
        if (StringUtils.isNumeric(lastIndex) && !lastIndex.equals("1")) {
            page = Integer.parseInt(lastIndex);
            query = String.join(" ", (String[]) ArrayUtils.remove(args, args.length-1));

        }
        BStatsUtil bStatsUtil = new BStatsUtil(api);
        EmbedBuilder embed = new EmbedBuilder();
        if (!(args.length >= 1 || (!channel.getIdAsString().equals(Constants.CHANNEL_OFFTOPIC) || !channel.getIdAsString().equals(Constants.CHANNEL_BOT)))) {
            command.addReaction("\uD83D\uDEAB");
            return; }
        else
            try {
                JsonNode search = bStatsUtil.makeRequest(MessageFormat.format(queryurl, query, page));
                StringBuilder result = new StringBuilder();
                DecimalFormat df = new DecimalFormat("#.##");
                for (Iterator<JsonNode> i = search.elements(); i.hasNext();) {
                    JsonNode resource = i.next();
                    double input = resource.get("rating").get("average").asDouble();
                    String message = df.format(input);
                    String name = String.format("[%s](https://www.spigotmc.org/resources/%s/) | %s \u2605", resource.get("name").asText(), resource.get("id").asText(), message);
                    String tag = String.format("```%s```", resource.get("tag").asText());
                    result.append(name).append(tag).append("\n");
                }

                embed.setAuthor("Spiget Search");
                embed.setColor(new Color(248, 151, 36));
                embed.addField("Results", result.toString());

                embed.setTimestampToNow();
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
                embed.setTitle("No resources found!").setColor(Color.RED);
            }

        channel.sendMessage(embed);
    }
}