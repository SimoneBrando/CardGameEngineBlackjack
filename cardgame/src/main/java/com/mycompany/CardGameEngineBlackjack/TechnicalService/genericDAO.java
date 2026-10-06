
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
            // La sessione si chiude automaticamente alla fine del blocco principale
            try (Session session = persistentManager.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    // Usiamo merge al posto di persist. Gestisce perfettamente le entità 
                    // "detached" che hai già salvato (come il Mazzo e il Dealer nel Seeder)
                    session.merge(entity);
                    transaction.commit();
                } catch (Exception e) {
                    // Il rollback ora avviene MENTRE la sessione è ancora aperta
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    // Stampa il VERO errore
                    e.printStackTrace(); 
                }
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