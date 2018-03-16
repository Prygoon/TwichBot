package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;

import java.util.List;

public interface UserDao {
    void addUser(User user);

    void updateUser(User user);

    void deleteDuplicateUser(User user);

    User getUserByNicknameAndChannel(String nickname, String channel);

    User getUserByIdAndChannel (Long userId, String channel);

    List<User> getTopFiveFlooders (String channel);

    List<User> getAllUsersWithoutTwitchId();

    List<User> getAllUsersWithoutRealNicknames();

    List<User> getDuplicateUsers(Long twitchId, String channel);
}
