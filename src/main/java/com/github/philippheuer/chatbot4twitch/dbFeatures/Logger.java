package com.github.philippheuer.chatbot4twitch.dbFeatures;

import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class Logger {

    public static void messageLoging(ChannelMessageEvent event) {
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
                event.getChannel().getName(),
                event.getChannel().getName());
        final String INSERT_NEW = String.format("INSERT INTO \"#%s\" (timest, nickname, message) VALUES (?, ?, ?)",
                event.getChannel().getName());
        final String MESSAGE_COUNTER = String.format("INSERT INTO users (nickname, channel, word_count)\n" +
                        "VALUES ('%s', '#%s', '%s')\n" +
                        "ON CONFLICT (nickname, channel) DO UPDATE SET\n" +
                        "message_count = (users.message_count + 1), word_count = (users.word_count + %s)",
                event.getUser().getDisplayName(),
                event.getChannel().getName(),
                event.getMessage().split(" ").length,
                event.getMessage().split(" ").length);
        DBWorker worker = new DBWorker();
        try {
            PreparedStatement statement = worker.getConnection().prepareStatement(CREATE_IF_NOT_EXISTS);
            statement.execute();
            statement = worker.getConnection().prepareStatement(INSERT_NEW);
            statement.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            statement.setString(2, event.getUser().getDisplayName());
            statement.setString(3, event.getMessage());
            statement.execute();
            if (!event.getUser().getDisplayName().toLowerCase().equals("nightbot")) {
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
