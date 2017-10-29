package com.github.philippheuer.chatbot4twitch.commands.general;

import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import java.util.Optional;

public class Help extends Command {
    /**
     * Initialize Command
     */
    public Help() {
        super();

        // Command Configuration
        setCommand("help");
        setCommandAliases(new String[]{});
        setCategory("general");
        setDescription("Показывает информацию о команде.");
        getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample("");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);

        // Parameters
        String cmdName = getParsedContent();

        // Get Command Info
        Optional<Command> cmd = getTwitchClient().getCommandHandler().getCommand(cmdName);

        // Command exists?
        if (cmd.isPresent()) {
            if (cmd.get().hasPermissions(messageEvent)) {
                // UserData has Permissions for Command
                String response = String.format("Команда: %s | Описание: %s", cmd.get().getCommand(), cmd.get().getDescription());
                sendMessageToChannel(messageEvent.getChannel().getName(), response);
            } else {
                // UserData has no permissions for this command
                return;
            }
        } else {
            /*String response;
            if (cmdName == null) {
                response = String.format("@%s %s - нет такой команды.", messageEvent.getUser().getDisplayName(), cmdName);
            } else {
                response = String.format("@%s Укажите название команды", messageEvent.getUser().getDisplayName());
            }
            sendMessageToChannel(messageEvent.getChannel().getName(), response);*/
            return;
        }
    }
}
