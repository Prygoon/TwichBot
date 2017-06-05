package com.github.philippheuer.chatbot4twitch.commands.general;

import me.philippheuer.twitch4j.chat.commands.Command;
import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.User;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Dice extends Command {
    /**
     * Initialize Command
     */
    public Dice() {
        super();

        // Command Configuration
        setCommand("dice");
        setCommandAliases(new String[]{"roll"});
        setCategory("general");
        setDescription("Roll a dice!");
        getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample("");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);

        // Roll the Dice
        Integer diceResult = ThreadLocalRandom.current().nextInt(1, 6);

        // Prepare Response
        String response = String.format("%s rolled %d out of 6.", messageEvent.getUser().getDisplayName(), diceResult);

        // Send Response
        sendMessageToChannel(messageEvent.getChannel().getName(), response);
    }
}
