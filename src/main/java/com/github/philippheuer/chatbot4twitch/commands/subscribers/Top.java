package com.github.philippheuer.chatbot4twitch.commands.subscribers;

import com.github.philippheuer.chatbot4twitch.dbFeatures.ChannelData;
import me.philippheuer.twitch4j.chat.commands.Command;
import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

public class Top extends Command {
    /**
     * Initialize Command
     */
    public Top() {
        super();

        // Command Configuration
        setCommand("top");
        setCommandAliases(new String[]{"topflooders", "flooders"});
        setCategory("moderation");
        setDescription("Displays top 5 flooders on channel.");
        getRequiredPermissions().add(CommandPermission.MODERATOR);
        setUsageExample("");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);
        ChannelData channelData = new ChannelData(messageEvent);
        // Prepare Response
        String response = channelData.getTopFlooders();
        // Send Response
        sendMessageToChannel(messageEvent.getChannel().getName(), response);
    }
}
