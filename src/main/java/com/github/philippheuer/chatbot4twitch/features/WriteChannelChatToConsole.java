package com.github.philippheuer.chatbot4twitch.features;

import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

public class WriteChannelChatToConsole {
    /**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    @EventSubscriber
    public void onChannelMessage(AbstractChannelEvent event) {
        String user = "";
        String message = "";

        if (event instanceof ChannelMessageEvent) {
            user = ((ChannelMessageEvent) event).getUser().getDisplayName();
            message = ((ChannelMessageEvent) event).getMessage();
        } else if (event instanceof ChannelMessageActionEvent) {
            user = ((ChannelMessageActionEvent) event).getUser().getDisplayName();
            message = "*" + ((ChannelMessageActionEvent) event).getMessage();
        }

        System.out.println("Channel [" + event.getChannel().getDisplayName() + "] - UserData[" + user + "] - Message [" + message + "]");
    }
}
