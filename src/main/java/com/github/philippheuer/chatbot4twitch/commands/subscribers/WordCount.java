
package com.github.philippheuer.chatbot4twitch.commands.subscribers;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.ChannelLogDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.UserDao;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

import javax.persistence.NoResultException;


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

        int wordCount = 0;
        int messageCount = 0;
        String commandTarget;
        String response;

        String nickname = messageEvent.getUser().getDisplayName();
        String channel = "#" + messageEvent.getChannel().getName();
        Long twitchId;
        UserDao userDao = new UserDao();
        ChannelLogDao logService = new ChannelLogDao();
        String firstDate = logService.getFirstData(channel);


        if (messageEvent.getMessage().split(" ").length > 1) {
            commandTarget = messageEvent.getMessage().split(" ")[1];
            twitchId = getTwitchClient().getUserEndpoint().getUserIdByUserName(commandTarget).get();
        } else {
            commandTarget = nickname;
            twitchId = messageEvent.getUser().getId();
        }

        try {
            User user = userDao.getUserByIdAndChannel(twitchId, channel);

            wordCount = user.getWordCount();
            messageCount = user.getMessageCount();
        } catch (NoResultException ignored) {

        }


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

