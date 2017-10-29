package com.github.philippheuer.chatbot4twitch.commands.moderation;

import com.github.philippheuer.chatbot4twitch.dbFeatures.ChannelData;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

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
        setDescription("Показывает топ 5 флудеров на канале.");
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

    @Override
    public Boolean hasPermissions(ChannelMessageEvent messageEvent) {
        if (messageEvent.getUser().getName().equals("prygoon")) {
            return true;
        } else {
            return super.hasPermissions(messageEvent);
        }
    }
}
