package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.dbFeatures.Logger;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

public class LogToDB {

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        Logger.messageLoging(event);
    }
}
