package dev.laarryy.wazowski.listeners;

import dev.laarryy.wazowski.Constants;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;

public class PrivateListener implements MessageCreateListener {

    DiscordApi api;
    private Optional<TextChannel> privateChannel;

    public PrivateListener(DiscordApi api) {
        this.api = api;
        privateChannel = api.getTextChannelById(Constants.CHANNEL_BOTDMS);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent ev) {
        String message = ev.getMessage().getContent();
        if (message.toLowerCase().startsWith("topsecret")|| ev.getChannel().getType() == ChannelType.PRIVATE_CHANNEL) {
            EmbedBuilder embed = new EmbedBuilder();
            String attachments = "";
            if (ev.getMessageAuthor().isYourself()) {
                return;
            }
            for (MessageAttachment attachment : ev.getMessage().getAttachments()) {
                attachments += "**Name:** " + attachment.getFileName() + "\n" + attachment.getUrl()+"\n";
            }

            embed.setAuthor(ev.getMessage().getAuthor());
            embed.setColor(Color.CYAN);
            embed.setThumbnail("https://i.imgur.com/FOhP8ug.png");
            embed.addInlineField("Author", ev.getMessage().getUserAuthor().get().getMentionTag());

            if (!(ev.getChannel().getType() == ChannelType.PRIVATE_CHANNEL)) {
                embed.addInlineField("Channel", String.format("<#%s>", ev.getChannel().getId()));
            }
            else
                embed.addInlineField("Channel", String.format("DM"));

            embed.addField("Message", "```"+message.replace("`", "")+"```");
            embed.addField("Attachments", attachments.isEmpty() ? "None" : attachments);

            embed.setTimestamp(Instant.now());

            if (ev.getMessageAuthor().isYourself()) {
                return;
            }

            privateChannel.get().sendMessage(embed);

            if (!(ev.getChannel().getType() == ChannelType.PRIVATE_CHANNEL)) {
                ev.getMessage().delete("Confidential");
            }
        }
    }
}
