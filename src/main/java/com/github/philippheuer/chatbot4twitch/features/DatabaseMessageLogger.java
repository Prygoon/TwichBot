package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.Calendar;

public class DatabaseMessageLogger {

    private ChannelLogService logService;
    private String displayNickname;
    private String nickname;
    private String channel;
    private String message;
    private ChannelLog log;

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        displayNickname = event.getUser().getDisplayName();
        nickname = event.getUser().getName();
        channel = "#" + event.getChannel().getName();
        message = event.getMessage();

        logService = new ChannelLogService();

        log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(displayNickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);

        increaseWordAndMessageCounts(event);
    }

    @EventSubscriber
    public void onChannelMessageAction(ChannelMessageActionEvent event) {
        displayNickname = event.getUser().getDisplayName();
        nickname = event.getUser().getName();
        channel = "#" + event.getChannel().getName();
        message = "[ACTION]" + event.getMessage();

        logService = new ChannelLogService();

        log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(displayNickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);

        increaseWordAndMessageCounts(event);
    }

    private void increaseWordAndMessageCounts(AbstractChannelEvent event) {

        User user;
        UserService userService = new UserService();
        Long twitchId = getTwitchIdFromEvent(event);

        try {
            user = userService.getUserByIdAndChannel(twitchId, channel);

            if (ChannelStatusCheck.isAlive(event)) {

                user.setMessageCount(user.getMessageCount() + 1);
                user.setWordCount(user.getWordCount() + message.split(" ").length);

            }

            if (user.getTwitchId() == 0) {
                user.setTwitchId(twitchId);
            }

            if (user.getNickname().equals("nothingnothing")) {
                user.setNickname(nickname);
            }

            if (!user.getDisplayNickname().equals(getDisplayNicknameFromEvent(event))) {
                user.setDisplayNickname(displayNickname);
            }

            userService.updateUser(user);

        } catch (NoResultException ex) {
            user = new User();
            user.setDisplayNickname(displayNickname);
            user.setNickname(nickname);
            user.setChannel(channel);
            user.setTwitchId(twitchId);

            userService.addUser(user);
        }
    }

    private Long getTwitchIdFromEvent(AbstractChannelEvent event) {

        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getUser().getId();
        }

        if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getUser().getId();
        }

        return 0L;
    }

    private String getDisplayNicknameFromEvent(AbstractChannelEvent event) {

        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getUser().getDisplayName();
        }

        if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getUser().getDisplayName();
        }

        return "";
    }
}

