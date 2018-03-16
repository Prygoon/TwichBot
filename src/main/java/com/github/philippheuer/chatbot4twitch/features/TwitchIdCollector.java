package com.github.philippheuer.chatbot4twitch.features;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;

import java.util.List;

public class TwitchIdCollector extends Command {

    public TwitchIdCollector() {
        super();

        // Command Configuration
        setCommand("collect");
        setCommandAliases(new String[]{});
        setCategory("moderation");
        setDescription(" ");
        getRequiredPermissions().add(CommandPermission.MODERATOR);
        getRequiredPermissions().add(CommandPermission.BROADCASTER);
        setUsageExample("");
    }

    @Override
    public void executeCommand(ChannelMessageEvent messageEvent) {
        super.executeCommand(messageEvent);

        if ((messageEvent.getUser().getName().equalsIgnoreCase("Prygoon")) && messageEvent.getChannel().getName().equalsIgnoreCase("dekeva")) {
            UserService userService = new UserService();
            List<User> allUsersWithoutTwitchId = userService.getAllUsersWithoutTwitchId();


            for (User anAllUsersWithoutTwitchId : allUsersWithoutTwitchId) {
                if (anAllUsersWithoutTwitchId.getDisplayNickname().matches("\\w[A-Za-z0-9_]*")) {
                    boolean nicknameFromTwitchIsPresent = getTwitchClient().getUserEndpoint().getUserByUserName(anAllUsersWithoutTwitchId.getDisplayNickname()).isPresent();
                    if (nicknameFromTwitchIsPresent) {

                        //String nickname = getTwitchClient().getUserEndpoint().getUserByUserName(anAllUsersWithoutTwitchId.getDisplayNickname()).get().getName();

                        Long twitchId = getTwitchClient().getUserEndpoint().getUserIdByUserName(anAllUsersWithoutTwitchId.getDisplayNickname()).orElse(0L);
                        anAllUsersWithoutTwitchId.setTwitchId(twitchId);
                        userService.updateUser(anAllUsersWithoutTwitchId);
                    }
                    /*try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }

            }
        }
    }
}
