package com.github.philippheuer.chatbot4twitch.checks;

import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

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
