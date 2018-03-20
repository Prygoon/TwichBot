
package com.github.philippheuer.chatbot4twitch.checks;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.ChannelLogService;
import com.github.philippheuer.chatbot4twitch.dbFeatures.service.UserService;
import me.philippheuer.twitch4j.events.Event;
import me.philippheuer.twitch4j.events.event.irc.IRCMessageEvent;

import javax.persistence.NoResultException;

import static com.github.philippheuer.chatbot4twitch.features.WordFilter.isMod;
import static com.github.philippheuer.chatbot4twitch.features.WordFilter.isSub;


public class BadWordCheck {
    public static boolean isQuest(String message) {
        return (message.toLowerCase().matches(".*(80|160).*(#\\d{4,5}).*") || message.toLowerCase().matches(".*(#\\d{4,5}).*(80|160).*"));
    }

    public static boolean isSilver(String message) {
        return message.matches(".*s1lverQ[1-3]?.*s1lverQ[1-3]?.*");
    }

    public static boolean isGoose(String message) {
        return ((message.length() / 2) > (getLettersAndNumbersCount(message)) && (message.length() > 25));
    }

    private static int getLettersAndNumbersCount(String message) {
        int count = 0;
        for (int i = 0; i < message.length(); i++) {
            if (String.valueOf(message.charAt(i)).matches("[a-zA-Z0-9А-Яа-я() .,!?\\-+{}*^~\\[\\]`@%:;\"'/\\\\_\\uD83C-\\uDBFF\\uDC00-\\uDFFF]"
                    /*"[ .,!?:^@;()*],[A-Z],[a-z],[А-Я],[а-я],[0-9]/u"*/
            )) {
                count++;
            }
        }
        return count;
    }

    public static int getCopyPasteCountFromDB(Event event) {
        String message = "";
        String nickname = "";
        String channel = "";
        Long twitchId = 0L;

        if (event instanceof IRCMessageEvent) {
            message = ((IRCMessageEvent) event).getMessage().orElse("");
            twitchId = ((IRCMessageEvent) event).getUserId();
            channel = "#" + ((IRCMessageEvent) event).getChannelName().orElse("");
            nickname = getDisplayNameFromRawMessage(((IRCMessageEvent) event).getRawMessage());

            if (nickname.equals("") && ((IRCMessageEvent) event).getClientName().isPresent()) {
                nickname = ((IRCMessageEvent) event).getClientName().get();
            }
        }

        try {
            if (!message.equals("")) {
                UserService userService = new UserService();
                ChannelLogService logService = new ChannelLogService();
                User user = userService.getUserByIdAndChannel(twitchId, channel);
                int copypasteCounter = user.getCopypasteCount();
                String previousMessage = logService.getPreviousMessage(nickname, channel);

                if (isSub(event) || isMod(event)) {
                    return 0;
                }

                if ((previousMessage != null) && ((leviAlg(previousMessage, message) < (message.length() / 4)) && (message.length() > 25))) {
                    user.setCopypasteCount(++copypasteCounter);
                    userService.updateUser(user);
                } else {
                    user.setCopypasteCount(0);
                    userService.updateUser(user);
                    copypasteCounter = 0;
                }
                return copypasteCounter;
            } else {
                return 0;
            }
        } catch (NoResultException ignored) {
            return 0;
        }
    }

    private static String getDisplayNameFromRawMessage(String message) {
        String[] strings = message.split("[=;]");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals("display-name")) {
                return strings[i + 1];
            }
        }
        return "";
    }

    private static int leviAlg(String S1, String S2) {
        int m = S1.length(), n = S2.length();
        int[] D1;
        int[] D2 = new int[n + 1];

        for (int i = 0; i <= n; i++)
            D2[i] = i;

        for (int i = 1; i <= m; i++) {
            D1 = D2;
            D2 = new int[n + 1];
            for (int j = 0; j <= n; j++) {
                if (j == 0) D2[j] = i;
                else {
                    int cost = (S1.charAt(i - 1) != S2.charAt(j - 1)) ? 1 : 0;
                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
                        D2[j] = D2[j - 1] + 1;
                    else if (D1[j] < D1[j - 1] + cost)
                        D2[j] = D1[j] + 1;
                    else
                        D2[j] = D1[j - 1] + cost;
                }
            }
        }
        return D2[n];
    }
}

