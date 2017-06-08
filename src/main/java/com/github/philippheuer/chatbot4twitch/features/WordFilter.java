package com.github.philippheuer.chatbot4twitch.features;

import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import static com.github.philippheuer.chatbot4twitch.checks.BadWordCheck.*;
import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isMod;
import static com.github.philippheuer.chatbot4twitch.checks.UserPermissionCheck.isSub;

public class WordFilter {

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) {
        final int MAX_SPAM_COUNT = 2;
        int copyasteCounter = copyPasteCount(event);
        if (isGoose(event.getMessage())) {
            if (isSub(event)) {
                event.sendMessage(String.format(".timeout %s 1 Гусь-гидра", event.getUser().getDisplayName()));
                event.sendMessage(String.format("@%s , не хулигань", event.getUser().getDisplayName()));
            } else if (isMod(event)) {
                event.sendMessage(String.format("@%s , не хулигань", event.getUser().getDisplayName()));
            } else {
                event.sendMessage(String.format(".timeout %s 600 Гусь-гидра", event.getUser().getDisplayName()));
                event.sendMessage(String.format("@%s , не хулигань", event.getUser().getDisplayName()));
            }
        } else if (isSilver(event.getMessage())) {
            if ((isSub(event)) || (isMod(event))) {
                event.sendMessage(String.format("@%s , фу, какая страшная рожа.", event.getUser().getDisplayName()));
            } else {
                event.sendMessage(String.format(".timeout %s 600 Рожа Сильвера", event.getUser().getDisplayName()));
                event.sendMessage(String.format("@%s , фу, какая страшная рожа.", event.getUser().getDisplayName()));
            }
        } else if (event.getMessage().toLowerCase().matches(".*卐.*"/*".*(\\b|[^a-zа-я0-9]|^)([aа][^a-zа-я0-9]?[yуu][^a-zа-я0-9]?[eе])($|[^a-zа-я0-9]).*"*/)) {
            if (isSub(event)) {
                event.sendMessage(String.format(".timeout %s 1 Свастика", event.getUser().getDisplayName()));
            } else if (isMod(event)) {
                event.sendMessage(String.format("@%s , не хулигань", event.getUser().getDisplayName()));
            } else {
                event.sendMessage(String.format(".timeout %s 1800 Свастика", event.getUser().getDisplayName()));
            }
        } else if (isQuest(event.getMessage())) {
            if (!(isSub(event) || isMod(event))) {
                if (event.getChannel().getName().equals("lenagol0vach")) {
                    event.sendMessage(String.format("@%s уходи, разводила.", event.getUser().getDisplayName()));
                } else {
                    event.sendMessage(String.format(".timeout %s 600 Дейлик", event.getUser().getDisplayName()));
                }
            }
        } else if (copyasteCounter == MAX_SPAM_COUNT) {
            event.sendMessage(String.format("@%s , прекрати копипастить.", event.getUser().getDisplayName()));

        } else if (copyasteCounter > MAX_SPAM_COUNT) {
            if (event.getChannel().getName().equals("lenagol0vach")) {
                event.sendMessage(String.format("@%s , прекрати копипастить.", event.getUser().getDisplayName()));
            } else {
                event.sendMessage(String.format(".timeout %s 600 Копипаста", event.getUser().getDisplayName()));
            }
        }
    }
}
