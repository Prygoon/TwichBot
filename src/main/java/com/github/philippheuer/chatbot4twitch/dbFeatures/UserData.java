package com.github.philippheuer.chatbot4twitch.dbFeatures;

import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {
    private String previousMessage;
    private int wordCount;
    private int messageCount;
    private int copypasteCount;
    private String commandTarget;
    private String nickname;

    public String getCommandTarget() {
        return commandTarget;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getCopypasteCount() {
        return copypasteCount;
    }

    public UserData(ChannelMessageEvent event) {
        if (event.getMessage().split("[ '\";()]").length > 1) {
            this.commandTarget = event.getMessage().split("[ '\";()]")[1].toLowerCase();
        } else {
            this.commandTarget = event.getUser().getDisplayName().toLowerCase();
        }
        this.previousMessage = getPreviousMessage(event);
        this.wordCount = getWordCountFromDB(event);
        this.messageCount = getMessageCountFromDB(event);
        this.copypasteCount = getCopypasteCountFromDB(event);
        this.nickname = getNicknameFromDB(event);
    }

    public String getNicknameFromDB(ChannelMessageEvent event) {
        final String GET_NICKNAME = String.format("SELECT nickname\n" +
                        "FROM users\n" +
                        "WHERE lower (nickname) LIKE '%s' AND channel LIKE '#%s'",
                commandTarget,
                event.getChannel().getName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_NICKNAME);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nickname = resultSet.getString("nickname");
            }
            dbWorker.getConnection().close();
            return nickname;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getPreviousMessage(ChannelMessageEvent event) {
        final String GET_PREVIOUS_MESSAGE = String.format("SELECT message FROM \"#%s\"\n" +
                        "WHERE nickname LIKE '%s' \n" +
                        "ORDER BY message_id desc LIMIT 1",
                event.getChannel().getName(),
                event.getUser().getDisplayName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_PREVIOUS_MESSAGE);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                previousMessage = resultSet.getString("message");
            }
            dbWorker.getConnection().close();
            return previousMessage;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    private int getWordCountFromDB(ChannelMessageEvent event) {
        final String GET_WORD_COUNT = String.format("SELECT word_count\n" +
                        "FROM users\n" +
                        "WHERE lower (nickname) LIKE '%s' AND channel LIKE '#%s'",
                commandTarget,
                event.getChannel().getName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_WORD_COUNT);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                wordCount = resultSet.getInt("word_count");
            }
            dbWorker.getConnection().close();
            return wordCount;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getMessageCountFromDB(ChannelMessageEvent event) {
        final String GET_MESSAGE_COUNT = String.format("SELECT message_count\n" +
                        "FROM users\n" +
                        "WHERE lower (nickname) LIKE '%s'\n" +
                        "AND channel LIKE '#%s'",
                commandTarget,
                event.getChannel().getName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_MESSAGE_COUNT);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                messageCount = resultSet.getInt("message_count");
            }
            dbWorker.getConnection().close();
            return messageCount;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getCopypasteCountFromDB(ChannelMessageEvent event) {
        final String GET_COPYPASTE_COUTER = String.format("SELECT copypaste_count FROM users\n" +
                "WHERE nickname LIKE '%s' \n" +
                "AND channel LIKE '#%s'", event.getUser().getDisplayName(), event.getChannel().getName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_COPYPASTE_COUTER);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                copypasteCount = resultSet.getInt("copypaste_count");
            }
            dbWorker.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return copypasteCount;
    }

    public void incrementCopypasteCount(ChannelMessageEvent event) {
        final String INCREMENT_COPYPASTE_COUNTER = String.format("UPDATE users \n" +
                        "SET copypaste_count = (users.copypaste_count + 1)\n" +
                        "WHERE nickname = '%s' AND channel = '#%s'",
                event.getUser().getDisplayName(),
                event.getChannel().getName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(INCREMENT_COPYPASTE_COUNTER);
            statement.execute();
            dbWorker.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void zeroingCopypasteCount(ChannelMessageEvent event) {
        final String ZEROING_COPYPASTE_COUNTER = String.format("UPDATE users\n" +
                        "SET copypaste_count = 0\n" +
                        "WHERE nickname = '%s' AND channel = '#%s'",
                event.getUser().getDisplayName(),
                event.getChannel().getName());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(ZEROING_COPYPASTE_COUNTER);
            statement.execute();
            dbWorker.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
