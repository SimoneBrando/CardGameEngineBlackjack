package com.mycompany.CardGameEngineBlackjack;

import com.mycompany.CardGameEngineBlackjack.Application.blackjackGameController;
import com.mycompany.CardGameEngineBlackjack.Domain.Partita;
import com.mycompany.CardGameEngineBlackjack.Domain.Mano;
import com.mycompany.CardGameEngineBlackjack.Domain.Carta;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainTestIntegrazione {
    public static void main(String[] args) {
        
        // Silenzia i log di Hibernate
        Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
        Logger.getLogger("com.mysql.cj.jdbc.Driver").setLevel(Level.WARNING);

        blackjackGameController controller = new blackjackGameController();
        Scanner scanner = new Scanner(System.in);
        
        int importoDaPuntare = 50;
        System.out.println("Avvio nuova partita...");
        // Sostituisci "1L" con l'ID reale di un giocatore salvato nel tuo DB
        Partita partita = controller.avviaNuovaPartita(1L, importoDaPuntare);
        
        System.out.println("\nID Partita: " + partita.getId());
        
        // Estraiamo le mani in modo sicuro
        Mano manoGiocatore = getManoGiocatore(partita);
        Mano manoDealer = partita.getDealer().getMano();
        
        // --- TESTING DELLA PUNTATA ---
        int fichesPuntate = manoGiocatore.getPuntata().getValoreTotale();
        int bilancioDopoPuntata = partita.getGiocatore().getBilancioFiches();
        int bilancioIniziale = bilancioDopoPuntata + fichesPuntate;
        
        System.out.println("\n--- GESTIONE FINANZIARIA (FASE INIZIALE) ---");
        System.out.println("Bilancio iniziale del giocatore: " + bilancioIniziale);
        System.out.println("Fiches puntate in questa mano: " + fichesPuntate);
        System.out.println("Bilancio temporaneo (dopo detrazione): " + bilancioDopoPuntata);
        
        System.out.println("\n--- SITUAZIONE INIZIALE ---");
        stampaMano("Giocatore", manoGiocatore);
        stampaMano("Dealer", manoDealer);
        
        // --- FASE INTERATTIVA DEL GIOCATORE ---
        boolean turnoGiocatore = true;
        
        while (turnoGiocatore && partita.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {
            System.out.print("\nScegli la tua mossa - Hit (H) o Stand (S): ");
            String mossa = scanner.nextLine().trim().toUpperCase();
            
            if (mossa.equals("H") || mossa.equals("HIT")) {
                System.out.println("\nHai chiamato Hit!");
                partita = controller.effettuaHit(partita.getId());
                
                manoGiocatore = getManoGiocatore(partita);
                stampaMano("Giocatore", manoGiocatore);
                
                if (manoGiocatore.getStatoMano() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoMano.SBALLATA) {
                    System.out.println("Hai sballato (Bust)!");
                    turnoGiocatore = false; 
                }
            } 
            else if (mossa.equals("S") || mossa.equals("STAND")) {
                System.out.println("\nHai chiamato Stand. Turno del Dealer...");
                partita = controller.effettuaStand(partita.getId());
                turnoGiocatore = false;
            } 
            else {
                System.out.println("Input non valido. Inserisci 'H' per pescare o 'S' per fermarti.");
            }
        }
        
        scanner.close();
        
        // Aggiorniamo la mano del dealer per vedere le nuove carte post-Stand
        manoDealer = partita.getDealer().getMano();
        System.out.println("\n--- SITUAZIONE FINALE ---");
        stampaMano("Dealer", manoDealer);
        
        System.out.println("\n--- RISULTATO E PAYOUT ---");
        System.out.println("Esito Finale: " + partita.getEsito());
        
        int bilancioFinale = partita.getGiocatore().getBilancioFiches();
        
        // Calcoliamo la differenza effettiva incassata dal giocatore
        if (bilancioFinale > bilancioDopoPuntata) {
            int incasso = bilancioFinale - bilancioDopoPuntata;
            int profittoNetto = incasso - fichesPuntate;
            System.out.println("Il giocatore incassa " + incasso + " fiches (Profitto netto: +" + profittoNetto + ")");
        } else if (bilancioFinale == bilancioDopoPuntata) {
            System.out.println("Nessun incasso. La puntata di " + fichesPuntate + " è andata persa.");
        }
        
        System.out.println("Bilancio Giocatore Finale: " + bilancioFinale);
    }

    private static Mano getManoGiocatore(Partita partita) {
        for (Mano m : partita.getMani()) {
            if (m.getGiocatore() != null) {
                return m;
            }
        }
        return null;
    }

    private static void stampaMano(String proprietario, Mano mano) {
        System.out.print(proprietario + " ha in mano: [");
        
        int punteggioVisibile = 0;
        boolean haCarteCoperte = false;
        
        for (int i = 0; i < mano.getCarte().size(); i++) {
            Carta c = mano.getCarte().get(i);
            if (c.getCoperta()) {
                System.out.print("CARTA COPERTA");
                haCarteCoperte = true;
            } else {
                System.out.print(c.getRango() + " di " + c.getSeme());
                punteggioVisibile += c.getValoriPossibili().get(0); 
            }
            
            if (i < mano.getCarte().size() - 1) {
                System.out.print(", ");
            }
        }
        
        if (haCarteCoperte) {
            System.out.println("] (Punteggio visibile: " + punteggioVisibile + ")");
        } else {
            System.out.println("] (Punteggio effettivo: " + mano.getPunteggio() + ")");
        }
    }
}