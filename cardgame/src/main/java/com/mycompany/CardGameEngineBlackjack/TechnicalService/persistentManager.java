package com.mycompany.CardGameEngineBlackjack.TechnicalService;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class persistentManager {

    private static SessionFactory sessionFactory;

    private persistentManager() {
        // Costruttore privato per impedire l'istanziazione
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Legge la configurazione dal file hibernate.cfg.xml
                sessionFactory = new Configuration().configure().buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Creazione della SessionFactory fallita: " + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}