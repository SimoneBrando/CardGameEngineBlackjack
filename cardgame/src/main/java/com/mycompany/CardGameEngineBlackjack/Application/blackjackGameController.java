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

        Partita partita = new Partita();
        partita.inizializza(giocatore, importoPuntata);
        partita.distribuisciCarteIniziali();
        
        // Salviamo il giocatore PRIMA della partita per consolidare le detrazioni o vincite immediate (es. Blackjack)
        giocatoreDao.update(giocatore);
        
        partita = partitaDao.save(partita);
        return partita;
    }

    public Partita effettuaHit(Long idPartita) {
        Partita partita = partitaDao.findById(idPartita);
        
        if (partita.getStato() != com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        partita.eseguiHitGiocatore();
        
        // Se l'Hit fa sballare il giocatore, la partita termina. Aggiorniamo il bilancio sul DB.
        if (partita.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.TERMINATA) {
            giocatoreDao.update(partita.getGiocatore());
        }
        
        partita = partitaDao.update(partita);
        return partita;
    }

    public Partita effettuaStand(Long idPartita) {
        Partita partita = partitaDao.findById(idPartita);
        
        if (partita.getStato() != com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy strategy = strategyFactory.getDealerStrategy("Standard17");
        
        partita.eseguiTurnoDealer(strategy);
        partita.determinaEsitoPartita();
        
        // Lo Stand chiude sempre la partita. Aggiorniamo il portafoglio consolidando vittorie/sconfitte.
        giocatoreDao.update(partita.getGiocatore());
        
        partita = partitaDao.update(partita);
        return partita;
    }


    
}