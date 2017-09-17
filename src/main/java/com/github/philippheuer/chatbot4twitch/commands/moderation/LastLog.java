
package com.github.philippheuer.chatbot4twitch.commands.moderation;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import me.philippheuer.twitch4j.chat.commands.Command;
import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import java.text.SimpleDateFormat;
import java.util.Collections;
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
        String nickname = messageEvent.getUser().getDisplayName();
        String channel = "#" + messageEvent.getChannel().getName();
        String commandTarget = messageEvent.getMessage().split(" ")[1].toLowerCase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
        ChannelLogService logService = new ChannelLogService();
        List<ChannelLog> lastLog = logService.getLastLog(channel, commandTarget);
        Collections.reverse(lastLog);

        if (lastLog.size() != 0) {
            for (ChannelLog log : lastLog) {
                String date = dateFormat.format(log.getTimestamp());
                String logNickname = log.getNickname();
                String logMessage = log.getMessage();
                String response = date + " " + logNickname + ":" + logMessage;
                // Send Response
                if ((nickname.toLowerCase().equals("prygoon") || (nickname.toLowerCase().equals(channel.toLowerCase())))) {
                    sendMessageToChannel(channel.substring(1), String.format("@%s " + response, nickname));
                } else {
                    getTwitchClient().getIrcClient().sendPrivateMessage(nickname, response);
                }
            }
        } else {
            if ((nickname.toLowerCase().equals("prygoon") || (nickname.toLowerCase().equals(channel.toLowerCase())))) {
                sendMessageToChannel(channel, String.format("@%s Нет информации.", nickname));
            } else {
                String response = String.format(" %s Нет информации.", nickname);
                getTwitchClient().getIrcClient().sendPrivateMessage(nickname, response);
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
