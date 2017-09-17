package com.github.philippheuer.chatbot4twitch.checks;

import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

public class UserPermissionCheck {
    public static boolean isMod(AbstractChannelEvent event) {
        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.BROADCASTER);
        } else if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.BROADCASTER);
        } else {
            return false;
        }
    }

    public static boolean isSub(AbstractChannelEvent event) {
        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.SUBSCRIBER)
                    || ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.BROADCASTER);
        } else if (event instanceof ChannelMessageActionEvent) {
            return ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.SUBSCRIBER)
                    || ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.BROADCASTER);
        } else {
            return false;
        }
    }
}
