package com.github.philippheuer.chatbot4twitch.checks;

import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;

public class ChannelStatusCheck {
    public static boolean isAlive(AbstractChannelEvent event) {
        return event.getClient().getStreamEndpoint().isLive(event.getChannel())
                || event.getClient().getStreamEndpoint().isReplaying(event.getChannel());
    }
}
