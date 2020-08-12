package dev.laarryy.wazowski.util;

import dev.laarryy.wazowski.Constants;
import org.javacord.api.entity.channel.Channel;

public class ChannelUtil {
    public static boolean isStaffChannel(Channel channel) {
        if (channel.getIdAsString().equals(Constants.CHANNEL_STAFFCHAT)
                || channel.getIdAsString().equals(Constants.CHANNEL_BOT)
                || channel.getIdAsString().equals(Constants.CHANNEL_BOTDMS)
        ) {
            return true;
        } else return false;
    }
    public static boolean isNonPublicChannel(Channel channel) {
        if (channel.getIdAsString().equals(Constants.CHANNEL_RICH)
                || channel.getIdAsString().equals(Constants.CHANNEL_PEBKAC)
                || channel.getIdAsString().equals(Constants.CHANNEL_STAFFCHAT)
                || channel.getIdAsString().equals(Constants.CHANNEL_BOT)
                || channel.getIdAsString().equals(Constants.CHANNEL_LEGITS)
        ) {
            return true;
        } else return false;
    }
    public static boolean isOffTopic(Channel channel) {
        if (channel.getIdAsString().equals(Constants.CHANNEL_OFFTOPIC)) {
            return true;
        } else return false;
    }
}