package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.ChannelLogDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.UserDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.UserCompositeId;
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
    private List<ChannelLog> channelLogs = new ArrayList<>();

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        ChannelLogDao logDao = new ChannelLogDao();
        String displayNickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = event.getMessage();

        increaseWordAndMessageCounts(event);

        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(displayNickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        try {
            logDao.addLog(log);
        } catch (Exception ex) {
            System.out.println("ОШИБКА!!!!!");
            logDao.getTransaction().rollback();
            logDao.getSession().close();
        }
        /*try {
            channelLogs.add(log);
            if (channelLogs.size() == 10) {
                logDao.addBatchOfLogs(channelLogs);
                channelLogs.clear();
            }
        } catch (Exception e) {
            System.out.println("ОШИБКА!!!!!");
            e.printStackTrace();
            logDao.getTransaction().rollback();
            logDao.closeSession();*/
    }


    @EventSubscriber
    public void onChannelMessageAction(ChannelMessageActionEvent event) {
        ChannelLogDao logDao = new ChannelLogDao();
        String displayNickname = event.getUser().getDisplayName();
        String channel = "#" + event.getChannel().getName();
        String message = "[ACTION]" + event.getMessage();

        ChannelLog log = new ChannelLog();
        log.setChannel(channel);
        log.setNickname(displayNickname);
        log.setMessage(message);
        log.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        //channelLogs.add(log);
        try {
            logDao.addLog(log);
        } catch (Exception ex) {
            logDao.getTransaction().rollback();
            logDao.getSession().close();
        }

        increaseWordAndMessageCounts(event);
    }

    private void increaseWordAndMessageCounts(AbstractChannelEvent event) {
        UserDao userDao = new UserDao();
        String message = getMessageFromAbstractChannelEvent(event);
        String channel = "#" + event.getChannel().getName();
        String nickname = getNicknameFromAbstractChannelEvent(event);
        String displayNickname = getDisplayNicknameFromAbstractChannelEvent(event);
        User user;
        User oldUser;

        Long twitchId = getTwitchIdFromAbstractChannelEvent(event);

        if (!(message.equals("") && nickname.equals("") && displayNickname.equals(""))) {
            try {
                user = userDao.getUserByTwitchIdAndChannel(twitchId, channel);
                oldUser = new User();
                oldUser.setDisplayNickname(user.getDisplayNickname());
                oldUser.setNickname(user.getNickname());
                oldUser.setMessageCount(user.getMessageCount());
                oldUser.setWordCount(user.getWordCount());

                if (!isKnownBot(nickname) && ChannelStatusCheck.isAlive(event)) {
                    user.setMessageCount(user.getMessageCount() + 1);
                    user.setWordCount(user.getWordCount() + message.split(" ").length);
                }

                if (!user.getDisplayNickname().equals(displayNickname)) {
                    user.setDisplayNickname(displayNickname);
                }

                if (!user.getNickname().equals(nickname)) {
                    user.setNickname(nickname);
                }

                if (!isUserNotChanged(user, oldUser)) {
                    userDao.updateUser(user);
                }

            } catch (NoResultException ex) {
                user = new User();
                user.setDisplayNickname(displayNickname);
                user.setNickname(nickname);
                UserCompositeId compositeId = new UserCompositeId();
                compositeId.setChannel(channel);
                compositeId.setTwitchId(twitchId);
                user.setCompositeId(compositeId);
                user.setMessageCount(1);
                user.setWordCount(message.split(" ").length);

                userDao.addUser(user);

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
                }
            } catch (NullPointerException ex) {
                userDao.getTransaction().rollback();
                userDao.getSession().close();
            }
        }
    }

    private Long getTwitchIdFromAbstractChannelEvent(AbstractChannelEvent event) {

        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getUser().getId();
        }

        if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getUser().getId();
        }

        return 0L;
    }

    private String getDisplayNicknameFromAbstractChannelEvent(AbstractChannelEvent event) {

        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getUser().getDisplayName();
        }

        if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getUser().getDisplayName();
        }

        return "";
    }

    private String getNicknameFromAbstractChannelEvent(AbstractChannelEvent event) {

        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getUser().getName();
        }

        if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getUser().getName();
        }

        return "";
    }

    private String getMessageFromAbstractChannelEvent(AbstractChannelEvent event) {

        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getMessage();
        }

        if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getMessage();
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

    private boolean isUserNotChanged(User user, User oldUser) {
        return user.getDisplayNickname().equals(oldUser.getDisplayNickname())
                && user.getNickname().equals(oldUser.getNickname())
                && (user.getMessageCount() == oldUser.getMessageCount())
                && (user.getWordCount() == oldUser.getWordCount());
    }
}

