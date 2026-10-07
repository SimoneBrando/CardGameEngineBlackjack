package com.mycompany.CardGameEngineBlackjack.Application;

import com.mycompany.CardGameEngineBlackjack.Domain.Partita;
import com.mycompany.CardGameEngineBlackjack.Domain.Giocatore;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.partitaDAO;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.giocatoreDAO;
import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategyFactory;
import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy;

public class blackjackGameController {

    private final partitaDAO partitaDao;
    private final giocatoreDAO giocatoreDao;
    private final dealerStrategyFactory strategyFactory;

    public blackjackGameController() {
        this.partitaDao = new partitaDAO();
        this.giocatoreDao = new giocatoreDAO();
        this.strategyFactory = new dealerStrategyFactory();
    }

    public Partita avviaNuovaPartita(Long idGiocatore, Integer importoPuntata) {
        Giocatore giocatore = giocatoreDao.findById(idGiocatore);
        if (giocatore.getBilancioFiches() < importoPuntata) {
            throw new IllegalArgumentException("Fiches insufficienti per la puntata.");
        }

        // Deleghiamo al Dominio (Pattern Creator) la costruzione di mazzo, mani e puntata
        Partita partita = new Partita();
        partita.inizializza(giocatore, importoPuntata);
        
        partita.distribuisciCarteIniziali();
        
        partitaDao.save(partita);
        return partita;
    }

    public Partita effettuaHit(Long idPartita) {
        Partita partita = partitaDao.findById(idPartita);
        
        if (partita.getStato() != statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        partita.eseguiHitGiocatore();
        partitaDao.update(partita);
        
        return partita;
    }

    public Partita effettuaStand(Long idPartita) {
        Partita partita = partitaDao.findById(idPartita);
        
        if (partita.getStato() != statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        // Il Controller usa il pattern Pure Fabrication per ottenere la strategia
        dealerStrategy strategy = strategyFactory.getDealerStrategy("Standard17");
        
        // Passa la strategia al dominio affinché il Dealer giochi il suo turno
        partita.eseguiTurnoDealer(strategy);
        partita.determinaEsitoPartita();
        
        partitaDao.update(partita);
        return partita;
    }


    
}