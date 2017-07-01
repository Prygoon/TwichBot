package com.github.philippheuer.chatbot4twitch.commands.general;

import me.philippheuer.twitch4j.chat.commands.Command;
import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import java.util.Set;

public class Commands extends Command {
    /**
     * Initialize Command
     */
    public Commands() {
        super();

        // Command Configuration
        setCommand("команды");
        setCommandAliases(new String[]{});
        setCategory("general");
        setDescription("Показывает все команды.");
        getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample("");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);
        Set<String> commandSet = getTwitchClient().getCommandHandler().getCommandMap().keySet();
        //int numberOfCommands = getTwitchClient().getCommandHandler().getCommandMap().size();
        StringBuilder builderMod = new StringBuilder("Для модераторов: ");
        StringBuilder builderSub = new StringBuilder("Для подписчиков: ");
        StringBuilder builderAll = new StringBuilder("Для всех: ");

        for (String command : commandSet
                ) {
            if (isForAll(command)) {
                builderAll.append("!").append(command).append(" ");
            }

            if (isForSubs(command)) {
                builderSub.append("!").append(command).append(" ");
            }

            if (isForMods(command)) {
                builderMod.append("!").append(command).append(" ");
            }
        }
        sendMessageToChannel(messageEvent.getChannel().getName(), builderMod.toString().trim() + ". " + builderSub.toString().trim() + ". " + builderAll.toString().trim() + ".");
    }

    private boolean isForAll(String command) {
        return (getTwitchClient().getCommandHandler().getCommand(command).isPresent())
                && (getTwitchClient().getCommandHandler().getCommand(command).get().getRequiredPermissions().contains(CommandPermission.EVERYONE));
    }

    private boolean isForSubs(String command) {
        return (getTwitchClient().getCommandHandler().getCommand(command).isPresent())
                && (getTwitchClient().getCommandHandler().getCommand(command).get().getRequiredPermissions().contains(CommandPermission.SUBSCRIBER));
    }

    private boolean isForMods(String command) {
        return (getTwitchClient().getCommandHandler().getCommand(command).isPresent())
                && (getTwitchClient().getCommandHandler().getCommand(command).get().getRequiredPermissions().contains(CommandPermission.MODERATOR))
                && !(getTwitchClient().getCommandHandler().getCommand(command).get().getRequiredPermissions().contains(CommandPermission.SUBSCRIBER));
    }

}

