
package com.mycompany.CardGameEngineBlackjack.TechnicalService;


import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class genericDAO<T> {

    private final Class<T> type;

    public genericDAO(Class<T> type) {
        this.type = type;
    }


    public T save(T entity) {
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Catturiamo l'oggetto unito (merged) che contiene l'ID
                T savedEntity = session.merge(entity);
                transaction.commit();
                return savedEntity; // Restituiamo l'oggetto completo
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException("Errore durante il salvataggio", e);
            }
        }
    }

    public T update(T entity) {
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T updatedEntity = session.merge(entity);
                transaction.commit();
                return updatedEntity;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException("Errore durante l'aggiornamento", e);
            }
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