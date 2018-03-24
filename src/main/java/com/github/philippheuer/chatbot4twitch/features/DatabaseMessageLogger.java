package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import com.github.philippheuer.chatbot4twitch.enums.Bots;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import org.hibernate.NonUniqueResultException;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseMessageLogger {

        @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        String displayNickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = event.getMessage();

        ChannelLogService logService = new ChannelLogService();

        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(displayNickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);

        increaseWordAndMessageCounts(event);
    }

    @EventSubscriber
    public void onChannelMessageAction(ChannelMessageActionEvent event) {
        String displayNickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = "[ACTION]" + event.getMessage();

        ChannelLogService logService = new ChannelLogService();

        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(displayNickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        logService.addLog(log);

        increaseWordAndMessageCounts(event);
    }

    private void increaseWordAndMessageCounts(AbstractChannelEvent event) {
        String message = "";
        String channel = "#" + event.getChannel().getName();
        String nickname = "";
        String displayNickname = "";
        User user;
        UserService userService = new UserService();
        Long twitchId = getTwitchIdFromEvent(event);

        if (event instanceof ChannelMessageEvent) {
            message = ((ChannelMessageEvent) event).getMessage();
            nickname = ((ChannelMessageEvent) event).getUser().getName();
            displayNickname = ((ChannelMessageEvent) event).getUser().getDisplayName();

        } else if (event instanceof ChannelMessageActionEvent) {
            message = ((ChannelMessageActionEvent) event).getMessage();
            nickname = ((ChannelMessageActionEvent) event).getUser().getName();
            displayNickname = ((ChannelMessageActionEvent) event).getUser().getDisplayName();
        }

        if (!(message.equals("") && nickname.equals("") && displayNickname.equals(""))) {
            try {
                user = userService.getUserByIdAndChannel(twitchId, channel);

                if (!isKnownBot(nickname) && ChannelStatusCheck.isAlive(event)) {
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
                user.setMessageCount(1);
                user.setWordCount(message.split(" ").length);

                userService.addUser(user);

            } catch (NonUniqueResultException ex) {
                List<User> duplicateUsers = userService.getDuplicateUsers(twitchId, channel);
                Optional<User> optionalWrongUser = duplicateUsers.stream().min(Comparator.comparingInt(User::getMessageCount));

                if (optionalWrongUser.isPresent()) {
                    User wrongUser = optionalWrongUser.get();
                    userService.deleteDuplicateUser(wrongUser);
                    System.out.println("DELETED");
                }
            }
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

    private boolean isKnownBot(String nickname) {

        Set<String> bots = Arrays.stream(Bots.values())
                .map(Bots::getNickname)
                .distinct()
                .collect(Collectors.toSet());

        return bots.contains(nickname);
    }
}

