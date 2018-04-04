package com.github.philippheuer.chatbot4twitch.commands.general;

import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import me.philippheuer.twitch4j.model.Follow;
import me.philippheuer.twitch4j.model.User;

import java.text.SimpleDateFormat;
import java.util.Optional;

public class FollowAge extends Command {
    /**
     * Initialize Command
     */
    public FollowAge() {
        super();

        // Command Configuration
        setCommand("followage");
        setCommandAliases(new String[]{"fa", "following"});
        setCategory("general");
        setDescription("Display's the first follow date!");
        getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample("");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        super.executeCommand(messageEvent);
        // Get Target (or self)
        User commandTarget = getCommandArgumentTargetUserOrSelf();

        // Get Follow Age
        Optional<Follow> follow = getTwitchClient().getUserEndpoint().checkUserFollowByChannel(commandTarget.getId(), messageEvent.getChannel().getId());
        // Response
        // Following

        String response = follow.map(follow1 -> String.format("@%s ты подписан на %s c %s!", commandTarget.getDisplayName(),
                messageEvent.getChannel().getDisplayName(),
                (dateFormat.format(follow1.getCreatedAt()))))
                // Not Following
                .orElseGet(() -> String.format("%s еще пока не фолловер", commandTarget.getDisplayName()));

        if (messageEvent.getPermissions().contains(CommandPermission.EVERYONE)) {
            sendMessageToChannel(messageEvent.getChannel().getName(), String.format(".w %s", commandTarget.getName()) + response);
        } else {
            sendMessageToChannel(messageEvent.getChannel().getName(), response);
        }
    }
}
