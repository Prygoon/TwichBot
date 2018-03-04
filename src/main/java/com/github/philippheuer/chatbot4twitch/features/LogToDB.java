package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.Calendar;

public class LogToDB {


    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        String nickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = event.getMessage();

        ChannelLogService logService = new ChannelLogService();
        UserService userService;
        User user;
        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(nickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);
        userService = new UserService();

        try {
            user = userService.getUserByNicknameAndChannel(nickname, channel);

            if (ChannelStatusCheck.isAlive(event)) {

                user.setMessageCount(user.getMessageCount() + 1);
                user.setWordCount(user.getWordCount() + message.split(" ").length);
            }

            userService.updateUser(user);

        } catch (NoResultException ex) {
            user = new User();
            user.setNickname(nickname);
            user.setChannel(channel);
            userService.addUser(user);
        }
    }

    @EventSubscriber
    public void onChannelMessageAction(ChannelMessageActionEvent event) {
        String nickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = "[ACTION]" + event.getMessage();

        ChannelLogService logService = new ChannelLogService();
        User user;
        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(nickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);
        UserService userService = new UserService();

        try {
            user = userService.getUserByNicknameAndChannel(nickname, channel);

            if (ChannelStatusCheck.isAlive(event)) {

                user.setMessageCount(user.getMessageCount() + 1);
                user.setWordCount(user.getWordCount() + message.split(" ").length);
            }

            userService.updateUser(user);

        } catch (NoResultException ex) {
            user = new User();
            user.setNickname(event.getUser().getDisplayName());
            userService.addUser(user);
        }
    }


}

