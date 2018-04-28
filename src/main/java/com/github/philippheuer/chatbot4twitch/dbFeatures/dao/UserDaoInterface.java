package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;

import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;

import java.util.List;

public interface UserDaoInterface {
    void addUser(User user);

    void updateUser(User user);

    void deleteUserById(User user);

    User getUserByNicknameAndChannel(String nickname, String channel);

    User getUserByTwitchIdAndChannel(Long userId, String channel);

    List<User> getTopFiveFlooders (String channel);

    List<User> getAllUsersWithoutTwitchId();

    List<User> getAllUsersWithoutRealNicknames();

    List<User> getDuplicateUsers(Long twitchId, String channel);
}
