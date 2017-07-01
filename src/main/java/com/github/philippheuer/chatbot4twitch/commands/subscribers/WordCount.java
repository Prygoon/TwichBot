package com.github.philippheuer.chatbot4twitch.commands.subscribers;

import com.github.philippheuer.chatbot4twitch.dbFeatures.ChannelData;
import com.github.philippheuer.chatbot4twitch.dbFeatures.UserData;
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

        UserData userData = new UserData(messageEvent);
        ChannelData channelData = new ChannelData(messageEvent);
        String nickname = userData.getNicknameFromDB(messageEvent);
        String firstDate = channelData.getFirstDate();
        String response;
        int wordCount = userData.getWordCount();
        int messageCount = userData.getMessageCount();
        // Prepare Response
        if ((nickname != null) && (wordCount != 0) && (messageCount != 0)) {
            response = String.format("@%s , %s сказал(а) уже %s слов в %s сообщениях с %s.",
                    messageEvent.getUser().getDisplayName(),
                    nickname,
                    wordCount,
                    messageCount,
                    firstDate);
        } else {
            response = String.format("@%s , %s еще ничего не сказал(а) на этом канале.",
                    messageEvent.getUser().getDisplayName(),
                    nickname);
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
