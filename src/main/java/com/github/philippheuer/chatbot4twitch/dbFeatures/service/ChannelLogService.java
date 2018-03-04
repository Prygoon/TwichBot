package com.github.philippheuer.chatbot4twitch.dbFeatures.service;

import com.github.philippheuer.chatbot4twitch.dbFeatures.SessionUtil;
import com.github.philippheuer.chatbot4twitch.dbFeatures.dao.ChannelLogDao;
import com.github.philippheuer.chatbot4twitch.dbFeatures.entity.ChannelLog;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ChannelLogService extends SessionUtil implements ChannelLogDao {

    @Override
    public void addLog(ChannelLog log) {
        openTransactionSession();

        Session session = getSession();
        session.save(log);

        closeTransactionSession();
    }

    @Override
    public String getFirstData(String channel) {
        openTransactionSession();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String sql = "select log.timestamp from ChannelLog log " +
                "where log.channel like :channel " +
                "order by log.id";

        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter("channel", channel);
        String firstDate = dateFormat.format((Timestamp) query.setMaxResults(1).getSingleResult());

        closeTransactionSession();

        return firstDate;
    }

    @Override
    public Stack<ChannelLog> getLastLog(String channel, String nickname) {
        Stack<ChannelLog> lastLogStack = new Stack<>();

        openTransactionSession();

        final int lastlogStringsQuantity = 3;
        String sql = "from ChannelLog log " +
                "where log.channel like :channel " +
                "and lower(log.nickname) like :nickname " +
                "order by log.timestamp desc";

        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter("channel", channel);
        query.setParameter("nickname", nickname.toLowerCase());
        lastLogStack.addAll(query.setMaxResults(lastlogStringsQuantity).list());

        closeTransactionSession();

        return lastLogStack;
    }

    @Override
    public String getPreviousMessage(String nickname, String channel) {
        openTransactionSession();

        String sql = "select log.message from ChannelLog log " +
                "where log.channel like :channel " +
                "and lower(log.nickname) like :nickname " +
                "order by log.id desc";

        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter("channel", channel);
        query.setParameter("nickname", nickname.toLowerCase());
        String previousMessage = (String) query.setFirstResult(1).setMaxResults(1).getSingleResult();

        closeTransactionSession();

        return previousMessage;
    }

}
