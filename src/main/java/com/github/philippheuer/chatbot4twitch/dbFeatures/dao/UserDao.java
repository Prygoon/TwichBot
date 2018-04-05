package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;

import com.github.philippheuer.chatbot4twitch.dbFeatures.SessionUtil;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.User;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.util.List;


public class UserDao extends SessionUtil implements UserDaoInterface {

    @Override
    public void addUser(User user) {
        openSession();

        Session session = getSession();
        session.save(user);

        closeSession();
    }

    @Override
    public void updateUser(User user) {
        openTransactionSession();

        Session session = getSession();
        session.update(user);

        closeTransactionSession();
    }

    @Override
    public void deleteUserById(User user) {
        String hql = "delete from User user " +
                "where user.id = :id";

        openTransactionSession();

        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setParameter("id", user.getId());
        query.executeUpdate();

        closeTransactionSession();
    }

    @Override
    public User getUserByNicknameAndChannel(String nickname, String channel) throws NoResultException, NonUniqueResultException {
        if (!(nickname.equals("") && channel.equals(""))) {
            String hql = "from User user " +
                    "where user.displayNickname like :displayNickname " +
                    "and user.channel like :channel";

            openSession();

            Session session = getSession();
            Query query = session.createQuery(hql);
            query.setParameter("displayNickname", nickname);
            query.setParameter("channel", channel);
            User user = (User) query.getSingleResult();

            closeSession();

            return user;
        } else return null;


    }

    @Override
    public User getUserByIdAndChannel(Long userId, String channel) throws NoResultException, NonUniqueResultException {
        if (!channel.equals("")) {
            String hql = "from User user " +
                    "where user.twitchId = :twitchId " +
                    "and user.channel like :channel";

            openSession();

            Session session = getSession();
            Query query = session.createQuery(hql);
            query.setParameter("twitchId", userId);
            query.setParameter("channel", channel);
            User user = (User) query.getSingleResult();

            closeSession();

            return user;
        } else return null;
    }

    @Override
    public List<User> getTopFiveFlooders(String channel) {


        final int topQuantity = 5;
        String hql = "from User user " +
                "where user.channel like :channel " +
                "order by user.wordCount desc";

        openSession();

        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setParameter("channel", channel);
        List<User> userList = query.setMaxResults(topQuantity).list();

        closeSession();

        return userList;
    }

    @Override
    public List<User> getAllUsersWithoutTwitchId() {
        String hql = "from User user " +
                "where user.twitchId = :twitchId";

        openSession();

        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setParameter("twitchId", 0L);

        return query.list();
    }

    @Override
    public List<User> getAllUsersWithoutRealNicknames() {
        String hql = "from User user " +
                "where user.nickname = :nickname";

        openSession();

        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setParameter("nickname", "nothingnothing");

        return query.list();
    }

    @Override
    public List<User> getDuplicateUsers(Long twitchId, String channel) {
        String hql = "from User user " +
                "where user.twitchId = :twitchId " +
                "and user.channel = :channel";

        openSession();

        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setParameter("twitchId", twitchId);
        query.setParameter("channel", channel);

        return query.list();
    }

}
