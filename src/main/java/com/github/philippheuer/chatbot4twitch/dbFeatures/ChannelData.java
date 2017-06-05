package com.github.philippheuer.chatbot4twitch.dbFeatures;

import me.philippheuer.twitch4j.events.event.ChannelMessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelData {
    private String channelName;
    private ChannelMessageEvent event;

    public ChannelData(ChannelMessageEvent event) {
        this.event = event;
        this.channelName = event.getChannel().getName();
    }

    public String getTopFlooders() {
        final String GET_TOP_FLOODERS = String.format("SELECT nickname, message_count, word_count\n" +
                "FROM users\n" +
                "WHERE channel LIKE '#%s'\n" +
                "ORDER BY word_count DESC\n" +
                "LIMIT 5", channelName);
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_TOP_FLOODERS);
            ResultSet resultSet = statement.executeQuery();
            List<String> nicknames = new ArrayList<>();
            List<String> messageCounts = new ArrayList<>();
            List<String> wordCounts = new ArrayList<>();
            StringBuilder builder = new StringBuilder(String.format("Нафлудили с %s. ", getFirstDate()));

            while (resultSet.next()) {
                nicknames.add(resultSet.getString("nickname"));
                messageCounts.add(resultSet.getString("message_count"));
                wordCounts.add(resultSet.getString("word_count"));
            }

            for (int i = 0; i < nicknames.size(); i++) {
                builder.append(String.format("%s. %s: %s сообщений, %s слов.", i + 1,
                        nicknames.get(i),
                        messageCounts.get(i),
                        wordCounts.get(i)));
                builder.append(" ");
            }
            return String.valueOf(builder);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Что-то пошло не так";
        }
    }

    public String getChannelName() {
        return channelName;
    }

    public String getFirstDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final String GET_FIRST_DATE = String.format("SELECT timest\n" +
                        "FROM \"#%s\"\n" +
                        "WHERE message_id = 1",
                channelName);
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_FIRST_DATE);
            ResultSet resultSet = statement.executeQuery();
            Timestamp timestamp = null;
            while (resultSet.next()) {
                timestamp = resultSet.getTimestamp("timest");
            }
            dbWorker.getConnection().close();
            return dateFormat.format(timestamp);
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public ArrayList<String> getLastLog() {
        ArrayList<String> list = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
        final String GET_LOG = String.format("SELECT timest, nickname, message\n" +
                        "FROM \"#%s\" WHERE lower (nickname) = '%s'\n" +
                        "ORDER BY message_id DESC\n" +
                        "LIMIT 3",
                event.getChannel().getName(),
                event.getMessage().split(" ")[1].toLowerCase());
        DBWorker dbWorker = new DBWorker();
        try {
            PreparedStatement statement = dbWorker.getConnection().prepareStatement(GET_LOG);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("timest");
                String nickname = resultSet.getString("nickname");
                String userMessage = resultSet.getString("message");
                list.add(dateFormat.format(timestamp) + " " + nickname + ": " + userMessage);
            }
            dbWorker.getConnection().close();
            Collections.reverse(list);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
