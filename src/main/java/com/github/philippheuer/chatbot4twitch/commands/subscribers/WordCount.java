
package com.github.philippheuer.chatbot4twitch.commands.subscribers;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.ChannelLogDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.UserDao;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import org.hibernate.NonUniqueResultException;

import javax.persistence.NoResultException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


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
        String commandTarget = "";
        String response;

        String displayNickname = messageEvent.getUser().getDisplayName();
        String nickname = messageEvent.getUser().getName();
        String channel = "#" + messageEvent.getChannel().getName();
        Long twitchId = 0L;
        UserDao userDao = new UserDao();
        User user;
        ChannelLogDao logService = new ChannelLogDao();
        String firstDate = logService.getFirstData(channel);

        try {
            if (messageEvent.getMessage().split(" ").length > 1) {
                commandTarget = messageEvent.getMessage().split(" ")[1];
                twitchId = getTwitchClient().getUserEndpoint().getUserIdByUserName(commandTarget).get();
            } else {
                commandTarget = displayNickname;
                twitchId = messageEvent.getUser().getId();
            }


            user = userDao.getUserByIdAndChannel(twitchId, channel);

            wordCount = user.getWordCount();
            messageCount = user.getMessageCount();

            // Prepare Response
            if ((displayNickname != null) && (wordCount != 0) && (messageCount != 0)) {
                response = String.format("@%s , %s сказал(а) уже %s слов в %s сообщениях с %s.",
                        messageEvent.getUser().getDisplayName(),
                        commandTarget,
                        wordCount,
                        messageCount,
                        firstDate);
            } else {
                response = String.format("@%s , %s еще ничего не сказал на этом канале.",
                        displayNickname,
                        commandTarget);
            }
            // Send Response
            sendMessageToChannel(messageEvent.getChannel().getName(), response);

        } catch (NoResultException ex) {
            user = new User();
            user.setDisplayNickname(displayNickname);
            user.setNickname(nickname);
            user.setChannel(channel);
            user.setTwitchId(twitchId);

            userDao.addUser(user);

            response = String.format("@%s , %s еще ничего не сказал на этом канале.",
                    displayNickname,
                    commandTarget);

            sendMessageToChannel(messageEvent.getChannel().getName(), response);

        } catch (NonUniqueResultException ex) {
            List<User> duplicateUsers = userDao.getDuplicateUsers(twitchId, channel);

            Optional<User> optionalWrongUser = duplicateUsers.stream().min(Comparator.comparingInt(User::getMessageCount));
            Optional<User> optionalRightUser = duplicateUsers.stream().max(Comparator.comparingInt(User::getMessageCount));

            if (optionalWrongUser.isPresent() && optionalRightUser.isPresent()) {
                User wrongUser = optionalWrongUser.get();
                User rightUser = optionalRightUser.get();
                rightUser.setMessageCount(wrongUser.getMessageCount() + rightUser.getMessageCount());
                rightUser.setWordCount(wrongUser.getWordCount() + rightUser.getWordCount());
                userDao.updateUser(rightUser);

                userDao.deleteUserById(wrongUser);
                System.out.println(wrongUser.getDisplayNickname() + " DELETED");

                response = String.format("@%s , %s сказал(а) уже %s слов в %s сообщениях с %s.",
                        messageEvent.getUser().getDisplayName(),
                        rightUser.getDisplayNickname(),
                        rightUser.getWordCount(),
                        rightUser.getMessageCount(),
                        firstDate);

                sendMessageToChannel(messageEvent.getChannel().getName(), response);
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

