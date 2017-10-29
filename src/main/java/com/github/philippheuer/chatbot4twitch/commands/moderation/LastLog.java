package com.github.philippheuer.chatbot4twitch.commands.moderation;

import com.github.philippheuer.chatbot4twitch.dbFeatures.ChannelData;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import java.util.List;

public class LastLog extends Command {
    /**
     * Initialize Command
     */
    public LastLog() {
        super();

        // Command Configuration
        setCommand("lastlog");
        setCommandAliases(new String[]{});
        setCategory("moderation");
        setDescription("Показывает 3 последних сообщения пользователя.");
        getRequiredPermissions().add(CommandPermission.MODERATOR);
        getRequiredPermissions().add(CommandPermission.BROADCASTER);
        setUsageExample("");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);
        // Prepare Response
        ChannelData channelData = new ChannelData(messageEvent);
        List<String> lastlog = channelData.getLastLog();
        if (lastlog.size() != 0) {
            for (String element : lastlog) {
                // Send Response
                if ((messageEvent.getUser()
                        .getDisplayName().toLowerCase()
                        .equals("prygoon") || (messageEvent.getUser()
                        .getDisplayName()
                        .toLowerCase()
                        .equals(messageEvent.getChannel().getName().toLowerCase())))) {
                    sendMessageToChannel(messageEvent.getChannel().getName(), String.format("@%s " + element, messageEvent.getUser().getDisplayName()));
                } else {
                    getTwitchClient().getMessageInterface().sendPrivateMessage(messageEvent.getUser().getName(), element);
                }
            }
        } else {
            if ((messageEvent.getUser()
                    .getDisplayName().toLowerCase()
                    .equals("prygoon") || (messageEvent.getUser()
                    .getDisplayName()
                    .toLowerCase()
                    .equals(messageEvent.getChannel().getName().toLowerCase())))) {
                sendMessageToChannel(messageEvent.getChannel().getName(), String.format("@%s Нет информации.", messageEvent.getUser().getDisplayName()));
            } else {
                String response = String.format(" %s Нет информации.", messageEvent.getUser().getDisplayName());
                getTwitchClient().getMessageInterface().sendPrivateMessage(messageEvent.getUser().getName(), response);
            }
        }
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

