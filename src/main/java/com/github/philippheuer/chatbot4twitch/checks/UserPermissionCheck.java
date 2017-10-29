package com.github.philippheuer.chatbot4twitch.checks;

import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

public class UserPermissionCheck {
    public static boolean isMod(ChannelMessageEvent event) {
        return ((event.getPermissions().contains(CommandPermission.MODERATOR))
                || (event.getPermissions().contains(CommandPermission.BROADCASTER)));
    }

    public static boolean isSub(ChannelMessageEvent event) {
        return ((event.getPermissions().contains(CommandPermission.SUBSCRIBER))
                || (event.getPermissions().contains(CommandPermission.MODERATOR))
                || (event.getPermissions().contains(CommandPermission.BROADCASTER)));
    }
}
