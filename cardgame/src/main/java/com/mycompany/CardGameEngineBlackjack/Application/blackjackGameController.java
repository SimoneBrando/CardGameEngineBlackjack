
package com.mycompany.CardGameEngineBlackjack.Application;

import com.mycompany.CardGameEngineBlackjack.Domain.Partita;
<<<<<<< HEAD
import com.mycompany.CardGameEngineBlackjack.Domain.Giocatore;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.partitaDAO;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.giocatoreDAO;
import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategyFactory;
import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.esitoPartita;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.*;
=======
>>>>>>> e46572a75879dc17e5a93597731c4970f2ff7854

public class blackjackGameController {

    private final GestorePartita gestorePartita;

    public blackjackGameController() {
        this.gestorePartita = new GestorePartita();
    }

    public Partita avviaNuovaPartita(Long idGiocatore, Integer importoPuntata) {
<<<<<<< HEAD
        Giocatore giocatore = giocatoreDao.findById(idGiocatore);
        if (giocatore.getBilancioFiches() < importoPuntata) {
            throw new IllegalArgumentException("Fiches insufficienti per la puntata.");
        }

        Partita partita = new Partita();
        partita.inizializza(giocatore, importoPuntata);
        partita.distribuisciCarteIniziali();
        
        // Se il giocatore fa subito Blackjack, la partita termina immediatamente e si aggiorna il giocatore e la partita
        if (partita.getStato() == statoPartita.TERMINATA) {
            giocatoreDao.update(giocatore);
            partitaDao.save(partita); // Salva le statistiche
        }
        
        return partita;
=======
        return gestorePartita.avviaNuovaPartita(idGiocatore, importoPuntata);
>>>>>>> e46572a75879dc17e5a93597731c4970f2ff7854
    }

    public Partita effettuaHit(Partita partita) {
<<<<<<< HEAD
        if (partita.getStato() != statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }

        partita.eseguiHitGiocatore();
        
        // Salva su DB solo se l'Hit fa sballare e chiude la mano
        if (partita.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.TERMINATA) {
            giocatoreDao.update(partita.getGiocatore());
            partitaDao.save(partita);
        }
        
        return partita;
=======
        return gestorePartita.effettuaHit(partita);
>>>>>>> e46572a75879dc17e5a93597731c4970f2ff7854
    }

    public Partita effettuaStand(Partita partita) {
        return gestorePartita.effettuaStand(partita);
    }
}
