package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;


import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;

import java.util.List;

public interface ChannelLogDao {

    void addLog(ChannelLog log);

    String getFirstData(String channel);

    List<ChannelLog> getLastLog(String channel, String nickname);

    String getPreviousMessage(String nickname, String channel);
}
