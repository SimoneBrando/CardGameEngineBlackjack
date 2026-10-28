package com.mycompany.CardGameEngineBlackjack.Application;

import com.mycompany.CardGameEngineBlackjack.Domain.Partita;
import com.mycompany.CardGameEngineBlackjack.Domain.Giocatore;
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
        
        // Se il giocatore fa subito Blackjack, la partita termina immediatamente
        if (partita.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.TERMINATA) {
            giocatoreDao.update(giocatore);
            partitaDao.save(partita); // Salva le statistiche
        }
        
        return partita;
    }

    // Riceve direttamente l'oggetto in RAM, non l'ID
    public Partita effettuaHit(Partita partita) {
        if (partita.getStato() != com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        partita.eseguiHitGiocatore();
        
        // Salva su DB solo se l'Hit fa sballare e chiude la mano
        if (partita.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.TERMINATA) {
            giocatoreDao.update(partita.getGiocatore());
            partitaDao.save(partita);
        }
        
        return partita;
    }

    public Partita effettuaStand(Partita partita) {
        if (partita.getStato() != com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        dealerStrategy strategy = strategyFactory.getDealerStrategy("Standard17");
        partita.eseguiTurnoDealer(strategy);
        partita.determinaEsitoPartita();
        
        // A fine turno, consolida il portafoglio e salva lo storico per le statistiche
        giocatoreDao.update(partita.getGiocatore());
        partitaDao.save(partita);
        
        return partita;
    }
}