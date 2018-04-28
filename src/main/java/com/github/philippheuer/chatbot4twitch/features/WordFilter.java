package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.UserDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import me.philippheuer.twitch4j.events.Event;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.irc.IRCMessageEvent;
import org.hibernate.NonUniqueResultException;

import javax.persistence.NoResultException;

import static com.github.philippheuer.chatbot4twitch.checks.BadWordCheck.*;

public class WordFilter {

    @EventSubscriber
    public void onChannelMessage(IRCMessageEvent event) {
        if (event.getCommandType().equals("PRIVMSG")) {
            UserDao userDao = new UserDao();
            String message = event.getMessage().orElse("");
            String nickname = event.getClientName().orElse("");
            String channel = event.getChannelName().orElse("");
            Long twitchId = event.getUserId();
            final int MAX_SPAM_COUNT = 2;
            int copyasteCounter = 0;

            if (!(isMod(event) || isSub(event) || isOwner(event))) {
                try {
                    User user = userDao.getUserByTwitchIdAndChannel(twitchId, "#" + channel);
                    copyPasteHandler(user, event);
                    userDao.updateUser(user);
                    copyasteCounter = user.getCopypasteCount();
                } catch (NoResultException | NonUniqueResultException | NullPointerException ex) {
                    userDao.getSession().close();
                }

            }

            if (!(message.equals("") && nickname.equals(""))) {
                if (isGoose(message)) {
                    if (isSub(event)) {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 1 Гусь-гидра", nickname));
                        event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , не хулигань", nickname));
                    } else if (isMod(event)) {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , не хулигань", nickname));
                    } else {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 600 Гусь-гидра", nickname));
                        event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , не хулигань", nickname));
                    }
                } else if (isSilver(message)) {
                    if ((isSub(event)) || (isMod(event))) {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , фу, какая мерзкая харя.", nickname));
                    } else {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 600 Рожа Сильвера", nickname));
                        event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , фу, какая мерзкая харя.", nickname));
                    }
                } else if (message.toLowerCase().matches(".*卐.*")) {
                    if (isSub(event)) {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 1 Свастика", nickname));
                    } else if (isMod(event)) {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , не хулигань", nickname));
                    } else {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 1800 Свастика", nickname));
                    }
                } else if (isQuest(message)) {
                    if (!(isSub(event) || isMod(event))) {
                        event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 600 Дейлик", nickname));
                    }
                } else if (copyasteCounter == MAX_SPAM_COUNT) {
                    event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , прекрати копипастить.", nickname));

                } else if (copyasteCounter > MAX_SPAM_COUNT) {
                    event.getClient().getMessageInterface().sendMessage(channel, String.format(".timeout %s 600 Копипаста", nickname));
                    event.getClient().getMessageInterface().sendMessage(channel, String.format("@%s , прекрати копипастить.", nickname));
                }
            }
        }
    }

    public static boolean isMod(Event event) {
        return event instanceof IRCMessageEvent && ((IRCMessageEvent) event).getBadges().containsKey("moderator");
    }

    public static boolean isSub(Event event) {
        return event instanceof IRCMessageEvent && ((IRCMessageEvent) event).getBadges().containsKey("subscriber");
    }

    public static boolean isOwner(Event event) {
        final long PRYGOONTWITCHID = 21562266L;
        if (event instanceof IRCMessageEvent) {
            if (((IRCMessageEvent) event).getBadges().containsKey("broadcaster")) {
                return true;
            } else return ((IRCMessageEvent) event).getUserId() == PRYGOONTWITCHID;
        } else {
            return false;
        }
    }
}
