package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import java.sql.Timestamp;
import java.util.Calendar;

public class LogToDB {


    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        String nickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = event.getMessage();

        ChannelLogService logService = new ChannelLogService();

        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(nickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);

        if (ChannelStatusCheck.isAlive(event)) {
            UserService userService = new UserService();

            User user = userService.getUserByNicknameAndChannel(nickname, channel);
            user.setMessageCount(user.getMessageCount() + 1);
            user.setWordCount(user.getWordCount() + message.split(" ").length);

            userService.updateUser(user);
        }
    }

    @EventSubscriber
    public void onChannelMessageAction(ChannelMessageActionEvent event) {
        String nickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = "[ACTION]" + event.getMessage();

        ChannelLogService logService = new ChannelLogService();

        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(nickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);

        if (ChannelStatusCheck.isAlive(event)) {
            UserService userService = new UserService();

            User user = userService.getUserByNicknameAndChannel(nickname, channel);
            user.setMessageCount(user.getMessageCount() + 1);
            user.setWordCount(user.getWordCount() + message.split(" ").length - 1);

            userService.updateUser(user);
        }
    }


}

