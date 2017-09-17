package com.github.philippheuer.chatbot4twitch.dbFeatures.service;

import com.github.philippheuer.chatbot4twitch.dbFeatures.SessionUtil;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.UserDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;


public class UserService extends SessionUtil implements UserDao {

    @Override
    public void addUser(User user) {
        openTransactionSession();

        Session session = getSession();
        session.save(user);

        closeTransactionSession();
    }

    @Override
    public void updateUser(User user) {
        openTransactionSession();

        Session session = getSession();
        session.update(user);

        closeTransactionSession();
    }

    @Override
    public User getUserByNicknameAndChannel(String nickname, String channel) {
        if (!(nickname.equals("") && channel.equals(""))) {
            openTransactionSession();

            String sql = "from User user " +
                    "where user.nickname like :nickname " +
                    "and user.channel like :channel";

            Session session = getSession();
            Query query = session.createQuery(sql);
            query.setParameter("nickname", nickname);
            query.setParameter("channel", channel);
            User user = (User) query.getSingleResult();

            closeTransactionSession();

            return user;
        } else return null;


    }

    @Override
    public List<User> getTopFiveFlooders(String channel) {
        openTransactionSession();

        final int topQuantity = 5;
        String hql = "from User user " +
                "where user.channel like :channel " +
                "order by user.wordCount desc";

        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setParameter("channel", channel);
        List<User> userList = query.setMaxResults(topQuantity).list();

        closeTransactionSession();

        return userList;
    }

}
