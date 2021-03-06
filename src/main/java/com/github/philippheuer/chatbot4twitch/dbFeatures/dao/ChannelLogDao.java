package com.github.philippheuer.chatbot4twitch.dbFeatures.dao;

import com.github.philippheuer.chatbot4twitch.dbFeatures.SessionUtil;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Stack;

public class ChannelLogDao extends SessionUtil implements ChannelLogDaoInterface {

    @Override
    public void addLog(ChannelLog log) {
        openSession();

        Session session = getSession();
        session.save(log);

        closeSession();
    }

    @Override
    public String getFirstData(String channel) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String sql = "select log.timestamp from ChannelLog log " +
                "where log.channel like :channel " +
                "order by log.id";
        openSession();

        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter("channel", channel);
        String firstDate = dateFormat.format((Timestamp) query.setMaxResults(1).getSingleResult());

        closeSession();

        return firstDate;
    }

    @Override
    public Stack<ChannelLog> getLastLog(String channel, String nickname) {
        Stack<ChannelLog> logs = new Stack<>();


        final int lastlogStringsQuantity = 3;
        String sql = "from ChannelLog log " +
                "where log.channel like :channel " +
                "and lower(log.nickname) like :nickname " +
                "order by log.timestamp desc";

        openSession();

        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter("channel", channel);
        query.setParameter("nickname", nickname.toLowerCase());
        logs.addAll(query.setMaxResults(lastlogStringsQuantity).list());

        closeSession();

        return logs;
    }

    @Override
    public String getPreviousMessage(String nickname, String channel) {


        String sql = "select log.message from ChannelLog log " +
                "where log.channel like :channel " +
                "and lower(log.nickname) like :nickname " +
                "order by log.id desc";

        openSession();
        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter("channel", channel);
        query.setParameter("nickname", nickname.toLowerCase());
        String previousMessage = (String) query.setFirstResult(1).setMaxResults(1).getSingleResult();

        closeSession();

        return previousMessage;
    }

}
