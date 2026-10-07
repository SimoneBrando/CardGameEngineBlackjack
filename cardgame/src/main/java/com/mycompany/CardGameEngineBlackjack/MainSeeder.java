package com.mycompany.CardGameEngineBlackjack;



import com.mycompany.CardGameEngineBlackjack.Domain.*;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainSeeder {

    public static void main(String[] args) {
        System.out.println("Inizio popolamento database...");

        // Inizializzazione DAO
        giocatoreDAO giocatoreDao = new giocatoreDAO();
        dealerDAO dealerDao = new dealerDAO();
        mazzoDAO mazzoDao = new mazzoDAO();
        partitaDAO partitaDao = new partitaDAO();

        // 1. Creazione Giocatori
        Giocatore francesco = new Giocatore(null, "Francesco Di Giacomo", 5000);
        Giocatore elena = new Giocatore(null, "Elena", 1500);
        Giocatore beatrice = new Giocatore(null, "Beatrice", 2000);
        
        giocatoreDao.save(francesco);
        giocatoreDao.save(elena);
        giocatoreDao.save(beatrice);

        // 2. Creazione Dealer
        Dealer dealer = new Dealer(null);
        dealerDao.save(dealer);

        // 3. Generazione del Mazzo con 52 Carte
        Mazzo mazzo = new Mazzo();
        List<Carta> carteGenerate = new ArrayList<>();

        for (Seme seme : Seme.values()) {
            for (Rango rango : Rango.values()) {
                List<Integer> valori;
                if (rango == Rango.ASSO) {
                    valori = Arrays.asList(1, 11);
                } else if (rango == Rango.JACK || rango == Rango.REGINA || rango == Rango.RE) {
                    valori = Arrays.asList(10);
                } else {
                    // Mappa gli enum DUE..DIECI al loro valore numerico effettivo
                    valori = Arrays.asList(rango.ordinal() + 1); 
                }
                
                Carta carta = new Carta(null, seme, rango, true, valori, mazzo);
                carteGenerate.add(carta);
            }
        }
        mazzo.setCarte(carteGenerate);
        mazzoDao.save(mazzo); // Il Cascade salverà automaticamente le 52 carte

        // 4. Creazione di una Partita di test
        Partita partita = new Partita(null, statoPartita.IN_CORSO, Turno.GIOCATORE, esitoPartita.IN_ATTESA, francesco, dealer, mazzo);
        
        // Assegnazione di una mano al giocatore
        Mano manoFrancesco = new Mano(null, 0, statoMano.IN_GIOCO, false, partita);
        manoFrancesco.setGiocatore(francesco);
        
        // Creazione di una puntata fittizia
        Puntata puntata = new Puntata(null, 25, manoFrancesco);
        Fiche fiche25 = new Fiche(null, taglioFiche.VENTICINQUE, 1, puntata);
        puntata.setFiches(Arrays.asList(fiche25));
        manoFrancesco.setPuntata(puntata);
        
        partita.setMani(Arrays.asList(manoFrancesco));
        
        // Salvo la partita (a cascata salverà Mano, Puntata e Fiche)
        partitaDao.save(partita);

        System.out.println("Popolamento completato con successo. Controlla phpMyAdmin.");
        
        // Chiusura sicura delle connessioni
        persistentManager.shutdown();
    }
}