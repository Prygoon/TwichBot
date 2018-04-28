package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;

import com.github.philippheuer.chatbot4twitch.dbFeatures.SessionUtil;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;


public class UserDao extends SessionUtil implements UserDaoInterface {


    @Override
    public void addUser(User user) {

        openTransactionSession();

        getSession().save(user);

        closeTransactionSession();

    }

    @Override
    public void updateUser(User user) {

        openTransactionSession();

        getSession().update(user);

        closeTransactionSession();

    }

    @Override
    public void deleteUserById(User user) {
        String hql = "delete from User user " +
                "where user.id = :id";

        openTransactionSession();

        Query query = getSession().createQuery(hql);
        query.setParameter("id", user.getId());
        query.executeUpdate();

        closeTransactionSession();
    }

    @Override
    public User getUserByNicknameAndChannel(String nickname, String channel) throws NoResultException, NonUniqueResultException {
        if (!(nickname.equals("") && channel.equals(""))) {
            User user;
            String hql = "from User user " +
                    "where user.displayNickname like :displayNickname " +
                    "and user.channel like :channel";

            openSession();

            Query query = getSession().createQuery(hql);
            query.setParameter("displayNickname", nickname);
            query.setParameter("channel", channel);
            user = (User) query.getSingleResult();

            closeSession();

            return user;

        } else return null;


    }

    @Override
    public User getUserByTwitchIdAndChannel(Long userId, String channel) throws NoResultException, NonUniqueResultException {

        if (!channel.equals("")) {


            String hql = "from User user " +
                    "where user.compositeId.channel = :channel " +
                    "and user.compositeId.twitchId = :twitchId";

            openSession();

            Query query = getSession().createQuery(hql);
            query.setParameter("twitchId", userId);
            query.setParameter("channel", channel);
            User user = (User) query.getSingleResult();

            closeSession();

            return user;
        } else return null;

    }

    @Override
    public List<User> getTopFiveFlooders(String channel) {

        List userList;
        final int topQuantity = 5;
        String hql = "from User user " +
                "where user.channel = :channel " +
                "order by user.wordCount desc";

        openSession();

        Query query = getSession().createQuery(hql);
        query.setParameter("channel", channel);
        userList = query.setMaxResults(topQuantity).list();

        closeSession();

        return userList;
    }

    @Override
    public List<User> getAllUsersWithoutTwitchId() {
        List list;
        String hql = "from User user " +
                "where user.twitchId = :twitchId";

        openSession();

        Query query = getSession().createQuery(hql);
        query.setParameter("twitchId", 0L);
        list = query.list();

        closeSession();

        return list;
    }

    @Override
    public List<User> getAllUsersWithoutRealNicknames() {
        List list;
        String hql = "from User user " +
                "where user.nickname = :nickname";

        openSession();

        Query query = getSession().createQuery(hql);
        query.setParameter("nickname", "nothingnothing");
        list = query.list();

        closeSession();

        return list;
    }

    @Override
    public List<User> getDuplicateUsers(Long twitchId, String channel) {
        List list;
        String hql = "from User user " +
                "where user.twitchId = :twitchId " +
                "and user.channel = :channel";

        openSession();

        Query query = getSession().createQuery(hql);
        query.setParameter("twitchId", twitchId);
        query.setParameter("channel", channel);
        list = query.list();

        closeSession();

        return list;
    }

}
