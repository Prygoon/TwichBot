package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.dbFeatures.DBLogger;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;

public class LogToDB {

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        DBLogger.messageLoging(event);
    }

    @EventSubscriber
    public void onChannelMessageAction (ChannelMessageActionEvent event){
        DBLogger.messageLoging(event);
    }
}
