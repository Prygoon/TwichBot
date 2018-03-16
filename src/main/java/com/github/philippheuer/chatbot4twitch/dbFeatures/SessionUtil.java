package com.github.philippheuer.chatbot4twitch.dbFeatures;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SessionUtil {

    private Session session;
    private Transaction transaction;

    public Session getSession() {
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    protected void openSession() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    protected void openTransactionSession() {
        openSession();
        transaction = session.beginTransaction();
    }

    protected void closeSession() {
        session.close();
    }

    protected void closeTransactionSession() {
        transaction.commit();
        closeSession();
    }
}
