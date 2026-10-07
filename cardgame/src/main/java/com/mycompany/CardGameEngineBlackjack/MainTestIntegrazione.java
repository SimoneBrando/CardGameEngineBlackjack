package com.mycompany.CardGameEngineBlackjack;

import com.mycompany.CardGameEngineBlackjack.Application.blackjackGameController;
import com.mycompany.CardGameEngineBlackjack.Domain.Partita;

public class MainTestIntegrazione {
    public static void main(String[] args) {
        blackjackGameController controller = new blackjackGameController();
        
        // Sostituisci "1L" con l'ID reale di un giocatore salvato nel tuo DB
        System.out.println("Avvio nuova partita...");
        Partita partita = controller.avviaNuovaPartita(1L, 50);
        
        System.out.println("Mano Giocatore Iniziale: " + partita.getMani().get(0).getPunteggio());
        
        // Simuliamo la scelta del giocatore
        // Se il punteggio è basso, chiamiamo un Hit
        if(partita.getMani().get(0).getPunteggio() < 17) {
             System.out.println("Il giocatore chiama Hit!");
             partita = controller.effettuaHit(partita.getId());
             System.out.println("Nuovo punteggio: " + partita.getMani().get(0).getPunteggio());
        }
        
        // Se non ha sballato, chiamiamo lo Stand e facciamo giocare il banco
        if(partita.getEsito() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.esitoPartita.IN_ATTESA) {
             System.out.println("Il giocatore chiama Stand. Turno del Dealer...");
             partita = controller.effettuaStand(partita.getId());
        }
        
        System.out.println("Esito Finale: " + partita.getEsito());
        System.out.println("Bilancio Giocatore: " + partita.getGiocatore().getBilancioFiches());
    }
}