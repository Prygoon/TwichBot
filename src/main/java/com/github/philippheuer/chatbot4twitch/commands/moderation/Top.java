
package com.github.philippheuer.chatbot4twitch.commands.moderation;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

import java.util.List;

public class Top extends Command {

    /**
     * Initialize Command
     */

    public Top() {
        super();

        // Command Configuration
        setCommand("top");
        setCommandAliases(new String[]{"topflooders", "flooders"});
        setCategory("moderation");
        setDescription("Показывает топ 5 флудеров на канале.");
        getRequiredPermissions().add(CommandPermission.MODERATOR);
        setUsageExample("");
    }


    /**
     * executeCommand Logic
     */

    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);

        String channel = "#" + messageEvent.getChannel().getName();
        UserService userService = new UserService();
        ChannelLogService logService = new ChannelLogService();

        // Prepare Response
        List<User> flooders = userService.getTopFiveFlooders(channel);
        StringBuilder builder = new StringBuilder(String.format("Нафлудили с %s. ", logService.getFirstData(channel)));

        int n = flooders.size();
        for (int i = 0; i < n; i++) {
            builder.append(String.format("%s. %s: %s сообщений, %s слов.",
                    i + 1,
                    flooders.get(i).getDisplayNickname(),
                    flooders.get(i).getMessageCount(),
                    flooders.get(i).getWordCount()));
            builder.append(" ");
        }


        String response = String.valueOf(builder).trim();
        // Send Response
        sendMessageToChannel(channel.substring(1), response);
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

