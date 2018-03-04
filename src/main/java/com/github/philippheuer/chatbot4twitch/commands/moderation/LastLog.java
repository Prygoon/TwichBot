
package com.github.philippheuer.chatbot4twitch.commands.moderation;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

import java.text.SimpleDateFormat;
import java.util.Stack;

import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isOwner;

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
        String nickname = messageEvent.getUser().getDisplayName();
        String channel = "#" + messageEvent.getChannel().getName();
        String commandTarget;
        if (messageEvent.getMessage().split(" ").length > 1) {
            commandTarget = messageEvent.getMessage().split(" ")[1];
        } else {
            commandTarget = messageEvent.getUser().getDisplayName();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
        ChannelLogService logService = new ChannelLogService();
        Stack<ChannelLog> lastLog = logService.getLastLog(channel, commandTarget);
        int lastLogSize = lastLog.size();

        if (!lastLog.empty()) {
            for (int i = 0; i < lastLogSize; i++) {
                ChannelLog log = lastLog.pop();
                String date = dateFormat.format(log.getTimestamp());
                String logNickname = log.getNickname();
                String logMessage = log.getMessage();
                String response = date + " " + logNickname + ":" + logMessage;
                // Send Response
                if (isOwner(nickname, channel.substring(1))) {
                    sendMessageToChannel(channel.substring(1), String.format("@%s " + response, nickname));
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendMessageToChannel(channel, response);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (isOwner(nickname, channel.substring(1))) {
                sendMessageToChannel(channel, String.format("@%s Нет информации.", nickname));
            } else {
                String response = String.format(" %s Нет информации.", nickname);
                getTwitchClient().getMessageInterface().sendPrivateMessage(nickname, response);
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
