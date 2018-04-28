package com.github.philippheuer.chatbot4twitch.dbFeatures;

import org.hibernate.FlushMode;
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

    public void openSession() {

        session = HibernateUtil.getSessionFactory()
                /*.withOptions()
                .autoClear(true)
                .flushMode(FlushMode.ALWAYS)*/
                .openSession();
    }

    public void openTransactionSession() {
        openSession();
        transaction = session.beginTransaction();
    }

    public void closeSession() {
        session.close();
    }

    public void closeTransactionSession() {
        session.getTransaction().commit();
        closeSession();
    }
}
