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
        String user = null;
        String message = null;

        if (event instanceof ChannelMessageEvent) {
            if (((ChannelMessageEvent) event).getUser().getDisplayName() == null) {
                user = ((ChannelMessageEvent) event).getUser().getName();
            } else {
                user = ((ChannelMessageEvent) event).getUser().getDisplayName();
            }
            message = ((ChannelMessageEvent) event).getMessage();
        } else if (event instanceof ChannelMessageActionEvent) {
            if (((ChannelMessageActionEvent) event).getUser().getDisplayName() == null) {
                user = ((ChannelMessageActionEvent) event).getUser().getName();
            } else {
                user = ((ChannelMessageActionEvent) event).getUser().getDisplayName();
            }
            message = "[ACTION]" + ((ChannelMessageActionEvent) event).getMessage();
        }
        if ((message != null) && (user != null)) {
            System.out.println("Channel [" + event.getChannel().getDisplayName() + "] - UserData[" + user + "] - Message [" + message + "]");

        }
    }
}
