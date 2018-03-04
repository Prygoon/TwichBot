package com.github.philippheuer.chatbot4twitch.checks;

import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

public class UserPermissionCheck {
    public static boolean isMod(AbstractChannelEvent event) {
        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.BROADCASTER);
        } else
            return event instanceof ChannelMessageActionEvent
                    && (((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.BROADCASTER));
    }

    public static boolean isSub(AbstractChannelEvent event) {
        if (event instanceof ChannelMessageEvent) {
            return ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.SUBSCRIBER)
                    || ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageEvent) event).getPermissions().contains(CommandPermission.BROADCASTER);
        } else {
            return event instanceof ChannelMessageActionEvent
                    && (((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.SUBSCRIBER)
                    || ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.MODERATOR)
                    || ((ChannelMessageActionEvent) event).getPermissions().contains(CommandPermission.BROADCASTER));
        }
    }

    public static boolean isOwner(String nickname, String channel) {
        return nickname.toLowerCase().equals("prygoon") || nickname.toLowerCase().equals(channel.toLowerCase());
    }
}
