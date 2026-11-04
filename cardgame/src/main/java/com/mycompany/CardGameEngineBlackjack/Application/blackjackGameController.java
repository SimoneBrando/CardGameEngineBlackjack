
package com.mycompany.CardGameEngineBlackjack.Application;

import com.mycompany.CardGameEngineBlackjack.Domain.Partita;

public class blackjackGameController {

    private final GestorePartita gestorePartita;

    public blackjackGameController() {
        this.gestorePartita = new GestorePartita();
    }

    public Partita avviaNuovaPartita(Long idGiocatore, Integer importoPuntata) {
        return gestorePartita.avviaNuovaPartita(idGiocatore, importoPuntata);
    }

    public Partita effettuaHit(Partita partita) {
        return gestorePartita.effettuaHit(partita);
    }

    public Partita effettuaStand(Partita partita) {
        return gestorePartita.effettuaStand(partita);
    }
}
