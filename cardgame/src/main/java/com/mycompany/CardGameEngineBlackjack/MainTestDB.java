package com.mycompany.CardGameEngineBlackjack;

import com.mycompany.CardGameEngineBlackjack.Application.blackjackGameController;
import com.mycompany.CardGameEngineBlackjack.Domain.Carta;
import com.mycompany.CardGameEngineBlackjack.Domain.Giocatore;
import com.mycompany.CardGameEngineBlackjack.Domain.Mano;
import com.mycompany.CardGameEngineBlackjack.Domain.Partita;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.giocatoreDAO;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.partitaDAO;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.persistentManager;

import java.util.List;
import java.util.Scanner;

/**
 * Permette di giocare da console una mano di Blackjack tramite blackjackGameController.
 * Giocatore e partita vengono salvati nel database (Hibernate).
 *
 * Prerequisito: MySQL/XAMPP avviato e database "cardgameblackjackengine2" già creato
 * (hibernate.hbm2ddl.auto=update crea le tabelle, ma non il database).
 */
public class MainTestDB {

    private static final int BILANCIO_INIZIALE = 1000;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        try {
            System.out.println("=== BLACKJACK - TEST CON DATABASE ===\n");

            giocatoreDAO giocatoreDao = new giocatoreDAO();
            partitaDAO partitaDao = new partitaDAO();
            blackjackGameController controller = new blackjackGameController();

            // 1. Creazione del giocatore e salvataggio nel DB (save() restituisce l'entità con l'id generato)
            Giocatore giocatore = giocatoreDao.save(new Giocatore(null, "Tester", BILANCIO_INIZIALE));
            Long idGiocatore = giocatore.getId();
            System.out.println("Giocatore creato nel DB: " + giocatore.getNome()
                    + " (id " + idGiocatore + ") - bilancio: " + giocatore.getBilancioFiches() + " fiches\n");

            // 2. Puntata e avvio della partita
            Partita partita = null;
            while (partita == null) {
                int puntata = leggiIntero(in, "Quante fiches vuoi puntare? ");
                try {
                    partita = controller.avviaNuovaPartita(idGiocatore, puntata);
                } catch (IllegalArgumentException e) {
                    System.out.println("Puntata non valida: " + e.getMessage());
                }
            }

            System.out.println("\n--- CARTE INIZIALI ---");
            stampaTavolo(partita, false);

            // 3. Turno del giocatore (se la partita non è già finita per Blackjack)
            while (partita.getStato() == statoPartita.IN_CORSO) {
                System.out.print("\nCosa vuoi fare? [H] Carta  [S] Stai: ");
                String scelta = in.nextLine().trim().toUpperCase();

                if (scelta.equals("H")) {
                    controller.effettuaHit(partita);
                    System.out.println("\n--- HIT ---");
                    stampaTavolo(partita, partita.getStato() == statoPartita.TERMINATA);
                } else if (scelta.equals("S")) {
                    controller.effettuaStand(partita);
                    System.out.println("\n--- STAND: TURNO DEL DEALER ---");
                    stampaTavolo(partita, true);
                } else {
                    System.out.println("Scelta non valida, digita H oppure S.");
                }
            }

            // 4. Riepilogo (la carta coperta del dealer viene mostrata solo a partita finita)
            partita.getDealer().getMano().scopriCartaCoperta();
            System.out.println("\n=== RISULTATO ===");
            stampaTavolo(partita, true);
            System.out.println("Esito: " + partita.getEsito());
            System.out.println("Importo scommesso: " + partita.getImportoScommesso());
            System.out.println("Blackjack: " + partita.getIsBlackjack()
                    + " | Giocatore sballato: " + partita.getIsPlayerBust()
                    + " | Dealer sballato: " + partita.getIsDealerBust());

            // 5. Verifica di quanto è stato realmente salvato nel DB
            System.out.println("\n=== VERIFICA SUL DATABASE ===");
            Giocatore dalDb = giocatoreDao.findById(idGiocatore);
            System.out.println("Bilancio salvato nel DB: " + dalDb.getBilancioFiches() + " fiches"
                    + " (variazione: " + (dalDb.getBilancioFiches() - BILANCIO_INIZIALE) + ")");

            List<Partita> tutte = partitaDao.findAll();
            Partita ultima = null;
            for (Partita p : tutte) {
                if (p.getGiocatore() != null && idGiocatore.equals(p.getGiocatore().getId())) {
                    ultima = p;
                }
            }
            if (ultima != null) {
                System.out.println("Partita salvata: id " + ultima.getId()
                        + " | esito " + ultima.getEsito()
                        + " | puntata " + ultima.getImportoScommesso()
                        + " | blackjack " + ultima.getIsBlackjack()
                        + " | player bust " + ultima.getIsPlayerBust()
                        + " | dealer bust " + ultima.getIsDealerBust());
            } else {
                System.out.println("ATTENZIONE: nessuna partita trovata nel DB per questo giocatore.");
            }

        } catch (Exception e) {
            System.err.println("Errore durante il test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            persistentManager.shutdown();
            in.close();
        }
    }

    private static int leggiIntero(Scanner in, String messaggio) {
        while (true) {
            System.out.print(messaggio);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero intero.");
            }
        }
    }

    /** Il punteggio del dealer viene mostrato solo a partita finita o a carta scoperta. */
    private static void stampaTavolo(Partita partita, boolean mostraPunteggioDealer) {
        Mano manoGiocatore = partita.getMani().get(0);
        Mano manoDealer = partita.getDealer().getMano();
        stampaMano("Giocatore", manoGiocatore, true);
        stampaMano("Dealer   ", manoDealer, mostraPunteggioDealer);
    }

    private static void stampaMano(String chi, Mano mano, boolean mostraPunteggio) {
        StringBuilder sb = new StringBuilder();
        for (Carta c : mano.getCarte()) {
            sb.append(c).append(" ");
        }
        String riga = chi + ": " + sb.toString().trim();
        if (mostraPunteggio) {
            riga += "  -> punteggio: " + mano.getPunteggio() + " (" + mano.getStatoMano() + ")";
        }
        System.out.println(riga);
    }
}