
package com.mycompany.CardGameEngineBlackjack.TechnicalService;

import com.mycompany.CardGameEngineBlackjack.Domain.Giocatore;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class giocatoreDAO {

    public void save(Giocatore giocatore) {
        Transaction transaction = null;
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(giocatore);
            transaction.commit();
            System.out.println("Giocatore salvato con successo!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    public Giocatore findById(Long id) {
        try (Session session = persistentManager.getSessionFactory().openSession()) {
            return session.get(Giocatore.class, id);
        }
    }
}