package com.github.philippheuer.chatbot4twitch.dbFeatures;

import me.philippheuer.twitch4j.events.event.AbstractChannelEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageActionEvent;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import java.sql.*;
import java.util.*;

import static com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck.isAlive;

public class DBLogger {

    public static void messageLoging(AbstractChannelEvent event) {
        List<String> bots = Arrays.asList("nightbot", "mooboot", "lilerine");
        String channelName = event.getChannel().getName();
        String message = "";
        String userName = "";
        int wordCount = 0;

        if (event instanceof ChannelMessageEvent) {
            message = ((ChannelMessageEvent) event).getMessage();
            userName = ((ChannelMessageEvent) event).getUser().getDisplayName();
            wordCount = ((ChannelMessageEvent) event).getMessage().split(" ").length;

        } else if (event instanceof ChannelMessageActionEvent) {
            message = "*" + ((ChannelMessageActionEvent) event).getMessage();
            userName = ((ChannelMessageActionEvent) event).getUser().getDisplayName();
            wordCount = ((ChannelMessageActionEvent) event).getMessage().split(" ").length;
        }

        final String CREATE_IF_NOT_EXISTS = String.format("CREATE TABLE if not exists \"#%s\" (\n" +
                        "  message_id serial NOT NULL,\n" +
                        "  timest timestamp with time zone NOT NULL,\n" +
                        "  nickname character varying(25) NOT NULL,\n" +
                        "  message character varying(500) NOT NULL,\n" +
                        "  CONSTRAINT \"#%s_pkey\" PRIMARY KEY (message_id)\n" +
                        ")\n" +
                        "WITH (\n" +
                        "  OIDS=FALSE\n" +
                        ")",
                channelName,
                channelName);
        final String INSERT_NEW = String.format("INSERT INTO \"#%s\" (timest, nickname, message) VALUES (?, ?, ?)",
                channelName);
        final String MESSAGE_COUNTER = String.format("INSERT INTO users (nickname, channel, word_count)\n" +
                        "VALUES ('%s', '#%s', '%s')\n" +
                        "ON CONFLICT (nickname, channel) DO UPDATE SET\n" +
                        "message_count = (users.message_count + 1), word_count = (users.word_count + %s)",
                userName,
                channelName,
                wordCount,
                wordCount);
        DBWorker worker = new DBWorker();
        try {
            PreparedStatement statement = worker.getConnection().prepareStatement(CREATE_IF_NOT_EXISTS);
            statement.execute();
            statement = worker.getConnection().prepareStatement(INSERT_NEW);
            statement.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            statement.setString(2, userName);
            statement.setString(3, message);
            statement.execute();
            if (!bots.contains(userName.toLowerCase()) && isAlive(event)) {
                statement = worker.getConnection().prepareStatement(MESSAGE_COUNTER);
                statement.execute();
                worker.getConnection().close();
            }

        } catch (
                SQLException e)

        {
            e.printStackTrace();
        }
    }
}
