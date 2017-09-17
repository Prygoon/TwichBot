
package com.github.philippheuer.chatbot4twitch.features;

import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import static com.github.philippheuer.chatbot4twitch.checks.BadWordCheck.*;
import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isMod;
import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isSub;

public class WordFilter {

    @EventSubscriber
    public void onChannelMessage(AbstractChannelEvent event) {
        final int MAX_SPAM_COUNT = 2;
        String userName = null;
        String message = null;

        if (event instanceof ChannelMessageEvent) {
            userName = ((ChannelMessageEvent) event).getUser().getDisplayName();
            message = ((ChannelMessageEvent) event).getMessage();
        } else if (event instanceof ChannelMessageActionEvent) {
            if (((ChannelMessageActionEvent) event).getUser().getDisplayName() == null) {
                userName = ((ChannelMessageActionEvent) event).getUser().getName();
            } else {
                userName = ((ChannelMessageActionEvent) event).getUser().getDisplayName();
            }
        }
        int copyasteCounter = copyPasteCount(event);

        if ((message != null) && (userName != null)) {
            if (isGoose(message)) {
                if (isSub(event)) {
                    event.sendMessage(String.format(".timeout %s 1 Гусь-гидра", userName));
                    event.sendMessage(String.format("@%s , не хулигань", userName));
                } else if (isMod(event)) {
                    event.sendMessage(String.format("@%s , не хулигань", userName));
                } else {
                    event.sendMessage(String.format(".timeout %s 600 Гусь-гидра", userName));
                    event.sendMessage(String.format("@%s , не хулигань", userName));
                }
            } else if (isSilver(message)) {
                if ((isSub(event)) || (isMod(event))) {
                    event.sendMessage(String.format("@%s , фу, какая мерзкая харя.", userName));
                } else {
                    event.sendMessage(String.format(".timeout %s 600 Рожа Сильвера", userName));
                    event.sendMessage(String.format("@%s , фу, какая мерзкая харя.", userName));
                }
            } else if (message.toLowerCase().matches(".*卐.*"
                    /*".*(\\b|[^a-zа-я0-9]|^)([aа][^a-zа-я0-9]?[yуu][^a-zа-я0-9]?[eе])($|[^a-zа-я0-9]).*"*/
            )) {
                if (isSub(event)) {
                    event.sendMessage(String.format(".timeout %s 1 Свастика", userName));
                } else if (isMod(event)) {
                    event.sendMessage(String.format("@%s , не хулигань", userName));
                } else {
                    event.sendMessage(String.format(".timeout %s 1800 Свастика", userName));
                }
            } else if (isQuest(message)) {
                if (!(isSub(event) || isMod(event))) {
                    event.sendMessage(String.format(".timeout %s 600 Дейлик", userName));
                }
            } else if (copyasteCounter == MAX_SPAM_COUNT) {
                event.sendMessage(String.format("@%s , прекрати копипастить.", userName));

            } else if (copyasteCounter > MAX_SPAM_COUNT) {
                event.sendMessage(String.format("@%s , прекрати копипастить.", userName));
                event.sendMessage(String.format(".timeout %s 600 Копипаста", userName));
            }
        }
    }
}


