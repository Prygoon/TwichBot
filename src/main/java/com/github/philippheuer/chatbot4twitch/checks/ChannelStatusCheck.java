package com.github.philippheuer.chatbot4twitch.checks;

import me.philippheuer.twitch4j.endpoints.StreamEndpoint;
import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Stream;

import java.util.Optional;

public class ChannelStatusCheck {
    public static boolean isAlive(AbstractChannelEvent event) {
        StreamEndpoint streamEndpoint = event.getClient().getStreamEndpoint();
        Optional<Stream> stream = streamEndpoint.getByChannel(event.getChannel());

        return stream.isPresent();
    }
}
