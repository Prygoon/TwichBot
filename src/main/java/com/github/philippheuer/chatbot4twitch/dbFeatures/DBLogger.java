package com.github.philippheuer.chatbot4twitch.dbFeatures;

import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.User;

import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.github.philippheuer.chatbot4twitch.checks.ChannelStatusCheck.isAlive;

public class DBLogger {

    public static void messageLoging(ChannelMessageEvent event) {
        List<String> bots = Arrays.asList("nightbot", "mooboot", "lilerine");
        String channelName = event.getChannel().getName();
        String userName = event.getUser().getDisplayName();
        int wordCount = event.getMessage().split(" ").length;
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
            statement.setString(3, event.getMessage());
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

    public static void selfLoging(User user, String message) {
        user.setDisplayName("Prygoon");
    }
}
