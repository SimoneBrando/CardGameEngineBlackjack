
package com.mycompany.CardGameEngineBlackjack.TechnicalService;


import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class genericDAO<T> {

    private final Class<T> type;

    public genericDAO(Class<T> type) {
        this.type = type;
    }

    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void update(T entity) {
        Transaction transaction = null;
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public T findById(Long id) {
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            return session.get(type, id);
        }
    }

    public List<T> findAll() {
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            return session.createQuery("from " + type.getName(), type).list();
        }
    }
}