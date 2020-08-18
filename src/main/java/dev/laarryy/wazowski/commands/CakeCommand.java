package dev.laarryy.wazowski.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.wazowski.Constants;
import dev.laarryy.wazowski.util.ChannelUtil;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CakeCommand implements CommandExecutor{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    List<String> responses = new ArrayList<>();
    String img = "https://i.imgur.com/253KTjq.gif";

    public CakeCommand() {
        File file = new File("data/cake_responses.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responses.add(line);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Command(aliases = {"!cake"}, usage = "!cake", description = "Maybe some cake?")
    public void onCommand(Message command, TextChannel channel, User user, String[] args) {
        if (!(ChannelUtil.isNonPublicChannel(channel) || ChannelUtil.isOffTopic(channel))) {
            command.addReaction("\uD83D\uDEAB");
            return;
        }

        if (args.length == 13) {
            channel.sendMessage(user.getMentionTag() + " 13 Ingredients? No thank you!");
            return;
        }
        channel.sendMessage(new EmbedBuilder().setTitle("What's the cake?").setImage(img)).thenAcceptAsync(message -> {
            String[] answer = responses.get(ThreadLocalRandom.current().nextInt(responses.size() -1)).split("\\|");
            EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode(answer[0])).setImage(answer[1]);
            message.getApi().getThreadPool().getScheduler().schedule(() -> message.edit(user.getMentionTag(), builder),4, TimeUnit.SECONDS);
        });
    }
}

