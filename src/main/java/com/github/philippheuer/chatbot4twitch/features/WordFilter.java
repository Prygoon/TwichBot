package com.github.philippheuer.chatbot4twitch.features;

import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import static com.github.philippheuer.chatbot4twitch.checks.BadWordCheck.*;
import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isMod;
import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isSub;

public class WordFilter {

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        final int MAX_SPAM_COUNT = 2;
        String userName = event.getUser().getDisplayName();
        int copyasteCounter = copyPasteCount(event);
        if (isGoose(event.getMessage())) {
            if (isSub(event)) {
                event.sendMessage(String.format(".timeout %s 1 Гусь-гидра", userName));
                event.sendMessage(String.format("@%s , не хулигань", userName));
            } else if (isMod(event)) {
                event.sendMessage(String.format("@%s , не хулигань", userName));
            } else {
                event.sendMessage(String.format(".timeout %s 600 Гусь-гидра", userName));
                event.sendMessage(String.format("@%s , не хулигань", userName));
            }
        } else if (isSilver(event.getMessage())) {
            if ((isSub(event)) || (isMod(event))) {
                event.sendMessage(String.format("@%s , фу, какая страшная рожа.", userName));
            } else {
                event.sendMessage(String.format(".timeout %s 600 Рожа Сильвера", userName));
                event.sendMessage(String.format("@%s , фу, какая страшная рожа.", userName));
            }
        } else if (event.getMessage().toLowerCase().matches(".*卐.*"/*".*(\\b|[^a-zа-я0-9]|^)([aа][^a-zа-я0-9]?[yуu][^a-zа-я0-9]?[eе])($|[^a-zа-я0-9]).*"*/)) {
            if (isSub(event)) {
                event.sendMessage(String.format(".timeout %s 1 Свастика", userName));
            } else if (isMod(event)) {
                event.sendMessage(String.format("@%s , не хулигань", userName));
            } else {
                event.sendMessage(String.format(".timeout %s 1800 Свастика", userName));
            }
        } else if (isQuest(event.getMessage())) {
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

