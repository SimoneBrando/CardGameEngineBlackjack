package com.mycompany.CardGameEngineBlackjack;

import com.mycompany.CardGameEngineBlackjack.Domain.*;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.*;

public class MainSeeder {

    public static void main(String[] args) {
        System.out.println("Inizio popolamento database...");

        // Inizializzazione dei soli DAO persistenti
        giocatoreDAO giocatoreDao = new giocatoreDAO();
        partitaDAO partitaDao = new partitaDAO();

        // 1. Creazione e salvataggio dei Giocatori
        Giocatore francesco = new Giocatore(null, "Francesco Di Giacomo", 5000);
        Giocatore elena = new Giocatore(null, "Elena", 1500);
        Giocatore beatrice = new Giocatore(null, "Beatrice", 2000);
        
        francesco = giocatoreDao.save(francesco);
        elena = giocatoreDao.save(elena);
        beatrice = giocatoreDao.save(beatrice);

        // 2. Esempio di uno storico partite di test (per le future statistiche)
        Partita partitaStorico = new Partita();
        partitaStorico.setGiocatore(francesco);
        partitaStorico.setEsito(esitoPartita.VITTORIA_GIOCATORE);
        
        partitaDao.save(partitaStorico);

        System.out.println("Popolamento completato con successo. Database allineato!");
        
        // Chiusura sicura delle connessioni
        persistentManager.shutdown();
    }
}