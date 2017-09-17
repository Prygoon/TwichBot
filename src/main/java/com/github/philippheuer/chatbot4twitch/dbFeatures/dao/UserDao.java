package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;

import java.util.List;

public interface UserDao {
    void addUser(User user);

    void updateUser(User user);

    User getUserByNicknameAndChannel(String nickname, String channel);

    List<User> getTopFiveFlooders (String channel);

}
