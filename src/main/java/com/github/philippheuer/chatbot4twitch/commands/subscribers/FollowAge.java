package com.github.philippheuer.chatbot4twitch.commands.subscribers;

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
        setCommandAliases(new String[]{"followsince", "following"});
        setCategory("subscribers");
        setDescription("Display's the first follow date!");
        getRequiredPermissions().add(CommandPermission.SUBSCRIBER);
        getRequiredPermissions().add(CommandPermission.MODERATOR);
        getRequiredPermissions().add(CommandPermission.BROADCASTER);
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
        // Not Following
        String response = follow.map(follow1 -> String.format("@%s ты подписан на %s c %s!", commandTarget.getDisplayName(),
                messageEvent.getChannel().getDisplayName(),
                (dateFormat.format(follow1.getCreatedAt()))))
                .orElseGet(() -> String.format("%s еще пока не фолловер", commandTarget.getDisplayName()));
        sendMessageToChannel(messageEvent.getChannel().getName(), response);
    }

    @Override
    public Boolean hasPermissions(ChannelMessageEvent messageEvent) {
        if (messageEvent.getUser().getName().equals("prygoon")) {
            return true;
        } else {
            return super.hasPermissions(messageEvent);
        }
    }
}
