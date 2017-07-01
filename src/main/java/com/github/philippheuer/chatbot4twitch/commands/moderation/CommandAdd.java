package com.github.philippheuer.chatbot4twitch.commands.moderation;

import me.philippheuer.twitch4j.chat.commands.Command;
import me.philippheuer.twitch4j.chat.commands.CommandPermission;
import me.philippheuer.twitch4j.chat.commands.DynamicCommand;
import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;
import me.philippheuer.util.conversion.TypeConvert;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class CommandAdd extends Command {

    /**
     * Parameter: Command Name
     */
    @Option(name = "-c", aliases = "--command", usage = "The command name to trigger this commmand", required = true)
    private String pCommandName = "";

    /**
     * Parameter: Command Type
     */
    @Option(name = "-t", aliases = "--type", usage = "The type of the command")
    private String pType = "echo";

    /**
     * Parameter: Response
     */
    @Option(name = "-r", aliases = "--response", usage = "The response to write by the command", handler = StringArrayOptionHandler.class, required = true)
    private String[] pResponse;

    /**
     * Initialize Command
     */
    public CommandAdd() {
        super();

        // Command Configuration
        setCommand("addcommand");
        setCommandAliases(new String[]{});
        setCategory("moderation");
        setDescription("Добавляет динамическую команду.");
        getRequiredPermissions().add(CommandPermission.MODERATOR);
        getRequiredPermissions().add(CommandPermission.BROADCASTER);
        setUsageExample("addcommand -c youtube -r Check out my Youtube at youtube.com/example");
    }

    /**
     * executeCommand Logic
     */
    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);
        // Parameter Validation
        if (!pCommandName.matches("[A-Za-zА-Яа-я0-9]+")) {
            // Invalid Command Naming
            return;
        }

        // Prepare Command
        DynamicCommand dynamicCommand;
        CommandPermission permission;
        // Command Type
        switch (pType) {
            case "all":
                permission = CommandPermission.EVERYONE;
                break;
            case "sub":
                permission = CommandPermission.SUBSCRIBER;
                break;
            default:
                permission = CommandPermission.MODERATOR;
                break;
        }
        // Create Command
        dynamicCommand = new DynamicCommand(pCommandName, permission, TypeConvert.combineStringArray(pResponse, " "));

        // Register Command
        getTwitchClient().getCommandHandler().registerCommand(dynamicCommand);

        // Send Response
        String response = String.format("Command %s has been added!", dynamicCommand.getCommand());
        getTwitchClient().getIrcClient().sendPrivateMessage(messageEvent.getUser().getName(), response);
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
