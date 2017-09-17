
package com.github.philippheuer.chatbot4twitch.commands.subscribers;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.chat.commands.Command;
import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

public class WordCount extends Command {

    /**
     * Initialize Command
     */

    public WordCount() {
        super();

        // Command Configuration
        setCommand("wordcount");
        setCommandAliases(new String[]{"wc"});
        setCategory("subscriber");
        setDescription("Показывает статистику пользователя.");
        getRequiredPermissions().add(CommandPermission.SUBSCRIBER);
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

        String nickname = messageEvent.getUser().getDisplayName();
        String channel = "#" + messageEvent.getChannel().getName();
        String commandTarget = messageEvent.getMessage().split(" ")[1];
        UserService userService = new UserService();
        ChannelLogService logService = new ChannelLogService();

        User user = userService.getUserByNicknameAndChannel(nickname, channel);
        String firstDate = logService.getFirstData(channel);

        String response;
        int wordCount = user.getWordCount();
        int messageCount = user.getMessageCount();
        // Prepare Response
        if ((nickname != null) && (wordCount != 0) && (messageCount != 0)) {
            response = String.format("@%s , %s сказал(а) уже %s слов в %s сообщениях с %s.",
                    messageEvent.getUser().getDisplayName(),
                    commandTarget,
                    wordCount,
                    messageCount,
                    firstDate);
        } else {
            response = String.format("@%s , %s еще ничего не сказал на этом канале.",
                    nickname,
                    commandTarget);
        }
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

