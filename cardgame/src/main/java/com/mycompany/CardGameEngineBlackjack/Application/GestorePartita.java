
package com.mycompany.CardGameEngineBlackjack.Application;

import com.mycompany.CardGameEngineBlackjack.Domain.*;
import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import com.mycompany.CardGameEngineBlackjack.TechnicalService.*;
import com.mycompany.CardGameEngineBlackjack.Application.Strategy.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorePartita {

    private final partitaDAO partitaDao;
    private final giocatoreDAO giocatoreDao;
    private final dealerStrategyFactory strategyFactory;

    public GestorePartita() {
        this.partitaDao = new partitaDAO();
        this.giocatoreDao = new giocatoreDAO();
        this.strategyFactory = new dealerStrategyFactory();
    }

    // =====================================================
    // 1. AVVIO PARTITA
    // =====================================================

    public Partita avviaNuovaPartita(Long idGiocatore, Integer importoPuntata) {

        Giocatore giocatore = giocatoreDao.findById(idGiocatore);

        if (giocatore == null) {
            throw new IllegalArgumentException("Giocatore non trovato.");
        }

        if (importoPuntata == null || importoPuntata <= 0) {
            throw new IllegalArgumentException("La puntata deve essere maggiore di zero.");
        }

        if (giocatore.getBilancioFiches() < importoPuntata) {
            throw new IllegalArgumentException("Fiches insufficienti.");
        }

        Partita partita = new Partita();

        inizializzaPartita(partita, giocatore, importoPuntata);
        distribuisciCarteIniziali(partita);

        // Se la partita termina subito (Blackjack iniziale),
        // aggiorniamo il database.
        if (partita.getStato() == statoPartita.TERMINATA) {
            salvaPartitaConclusa(partita, giocatore);
        }

        return partita;
    }

    private void inizializzaPartita(
            Partita partita,
            Giocatore giocatore,
            Integer importoPuntata) {

        partita.setGiocatore(giocatore);
        partita.setDataOra(LocalDateTime.now());
        partita.setStato(statoPartita.IN_CORSO);
        partita.setTurnoCorrente(Turno.GIOCATORE);
        partita.setEsito(esitoPartita.IN_ATTESA);
        partita.setImportoScommesso(importoPuntata);

        // Creazione e mescolamento del mazzo
        Mazzo mazzo = creaMazzo(1);
        mescolaMazzo(mazzo);
        partita.setMazzo(mazzo);

        // Inizializzazione della mano del giocatore
        Mano manoGiocatore = new Mano();
        manoGiocatore.setPartita(partita);
        manoGiocatore.setGiocatore(giocatore);
        manoGiocatore.setStatoMano(statoMano.IN_GIOCO);
        manoGiocatore.setCarte(new ArrayList<>());

        Puntata puntata = new Puntata();
        puntata.setValoreTotale(importoPuntata);
        puntata.setMano(manoGiocatore);

        manoGiocatore.setPuntata(puntata);

        List<Mano> mani = new ArrayList<>();
        mani.add(manoGiocatore);

        partita.setMani(mani);

        // Aggiornamento delle mani transient del giocatore
        if (giocatore.getMani() == null) {
            giocatore.setMani(new ArrayList<>());
        }

        giocatore.getMani().clear();
        giocatore.getMani().add(manoGiocatore);

        // Detrazione della puntata
        giocatore.setBilancioFiches(
                giocatore.getBilancioFiches() - importoPuntata
        );

        // Inizializzazione dealer
        Dealer dealer = new Dealer();

        Mano manoDealer = new Mano();
        manoDealer.setPartita(partita);
        manoDealer.setDealer(dealer);
        manoDealer.setStatoMano(statoMano.IN_GIOCO);
        manoDealer.setCarte(new ArrayList<>());

        dealer.setMano(manoDealer);
        partita.setDealer(dealer);
    }

    // =====================================================
    // 2. GESTIONE DEL MAZZO
    // =====================================================

    private Mazzo creaMazzo(int numeroMazzi) {

        Mazzo mazzo = new Mazzo();

        mazzo.setNumeroMazzi(numeroMazzi);
        mazzo.setStato(statoMazzo.PRONTO);

        List<Carta> carte = new ArrayList<>();

        for (int i = 0; i < numeroMazzi; i++) {

            for (Seme seme : Seme.values()) {

                for (Rango rango : Rango.values()) {

                    Carta carta = new Carta();

                    carta.setSeme(seme);
                    carta.setRango(rango);
                    carta.setCoperta(false);
                    carta.setMazzo(mazzo);

                    List<Integer> valori = new ArrayList<>();

                    switch (rango) {

                        case ASSO:
                            valori.add(1);
                            valori.add(11);
                            break;

                        case JACK:
                        case REGINA:
                        case RE:
                            valori.add(10);
                            break;

                        default:
                            valori.add(rango.ordinal() + 1);
                            break;
                    }

                    carta.setValoriPossibili(valori);
                    carte.add(carta);
                }
            }
        }

        mazzo.setCarte(carte);

        return mazzo;
    }

    private void mescolaMazzo(Mazzo mazzo) {

        Collections.shuffle(mazzo.getCarte());
        mazzo.setStato(statoMazzo.PRONTO);
    }

    private Carta pescaCarta(Mazzo mazzo) {

        if (mazzo.getCarte() == null || mazzo.getCarte().isEmpty()) {

            mazzo.setStato(statoMazzo.ESAURITO);

            throw new IllegalStateException("Il mazzo è esaurito.");
        }

        Carta carta = mazzo.getCarte().remove(
                mazzo.getCarte().size() - 1
        );

        carta.setMazzo(null);

        return carta;
    }

    // =====================================================
    // 3. DISTRIBUZIONE CARTE INIZIALI
    // =====================================================

    private void distribuisciCarteIniziali(Partita partita) {

        Mano manoGiocatore = getManoGiocatore(partita);
        Mano manoDealer = partita.getDealer().getMano();

        // Due carte al giocatore
        aggiungiCarta(manoGiocatore, pescaCarta(partita.getMazzo()));

        aggiungiCarta(manoGiocatore, pescaCarta(partita.getMazzo()));

        // Due carte al dealer
        aggiungiCarta(manoDealer, pescaCarta(partita.getMazzo()));

        Carta secondaCartaDealer = pescaCarta(partita.getMazzo());
        secondaCartaDealer.setCoperta(true);

        aggiungiCarta(manoDealer, secondaCartaDealer);

        calcolaPunteggio(manoGiocatore);
        calcolaPunteggio(manoDealer);

        boolean blackjackGiocatore = haBlackjackNaturale(manoGiocatore);
        boolean blackjackDealer = haBlackjackNaturale(manoDealer);

        // Verifica dei Blackjack naturali
        if (blackjackGiocatore || blackjackDealer) {

            scopriCarteDealer(manoDealer);

            if (blackjackGiocatore && blackjackDealer) {
                partita.setEsito(esitoPartita.PAREGGIO);
                getManoGiocatore(partita).setStatoMano(statoMano.BLACKJACK);
                manoDealer.setStatoMano(statoMano.BLACKJACK);
                restituisciPuntata(partita);
            }
            else if (blackjackGiocatore) {
                partita.setEsito(esitoPartita.VITTORIA_GIOCATORE);
                manoGiocatore.setStatoMano(statoMano.BLACKJACK);
                accreditaVincita(partita, true);
            }
            else {
                partita.setEsito(esitoPartita.VITTORIA_DEALER);
                manoDealer.setStatoMano(statoMano.BLACKJACK);
            }

            partita.setStato(statoPartita.TERMINATA);
            popolaStatisticheFinali(partita);

            return;
        }

        partita.setTurnoCorrente(Turno.GIOCATORE);
    }

    // =====================================================
    // 4. GESTIONE MANI E PUNTEGGI
    // =====================================================

    private void aggiungiCarta(Mano mano, Carta carta) {

        mano.getCarte().add(carta);
    }

    private int calcolaPunteggio(Mano mano) {

        int punteggio = 0;
        int numeroAssi = 0;

        for (Carta carta : mano.getCarte()) {

            if (carta.getRango() == Rango.ASSO) {
                numeroAssi++;
                punteggio += 11;
            }
            else {
                punteggio += carta.getValoriPossibili().get(0);
            }
        }

        // Gli assi passano da 11 a 1 quando necessario
        while (punteggio > 21 && numeroAssi > 0) {

            punteggio -= 10;
            numeroAssi--;
        }

        mano.setPunteggio(punteggio);

        if (punteggio > 21) {
            mano.setStatoMano(statoMano.SBALLATA);
        }
        else if (punteggio == 21 && mano.getCarte().size() == 2) {
            mano.setStatoMano(statoMano.BLACKJACK);
        }
        else {
            mano.setStatoMano(statoMano.IN_GIOCO);
        }

        return punteggio;
    }

    private boolean haAssoSoft(Mano mano) {

        int punteggioConAssiAUno = 0;
        boolean contieneAsso = false;

        for (Carta carta : mano.getCarte()) {

            if (carta.getRango() == Rango.ASSO) {
                contieneAsso = true;
                punteggioConAssiAUno += 1;
            }
            else {
                punteggioConAssiAUno += carta.getValoriPossibili().get(0);
            }
        }

        return contieneAsso && punteggioConAssiAUno + 10 <= 21;
    }

    private boolean haBlackjackNaturale(Mano mano) {

        return mano.getCarte().size() == 2
                && mano.getPunteggio() == 21;
    }

    private void scopriCarteDealer(Mano manoDealer) {

        for (Carta carta : manoDealer.getCarte()) {
            carta.setCoperta(false);
        }
    }

    private Mano getManoGiocatore(Partita partita) {

        for (Mano mano : partita.getMani()) {

            if (mano.getGiocatore() != null) {
                return mano;
            }
        }

        throw new IllegalStateException("Mano del giocatore non trovata.");
    }

    // =====================================================
    // 5. TURNO DEL GIOCATORE - HIT
    // =====================================================

    public Partita effettuaHit(Partita partita) {

        verificaPartitaInCorso(partita);

        Mano manoGiocatore = getManoGiocatore(partita);

        Carta carta = pescaCarta(partita.getMazzo());

        aggiungiCarta(manoGiocatore, carta);

        int punteggio = calcolaPunteggio(manoGiocatore);

        if (punteggio > 21) {

            partita.setEsito(esitoPartita.VITTORIA_DEALER);
            partita.setStato(statoPartita.TERMINATA);

            popolaStatisticheFinali(partita);

            salvaPartitaConclusa(partita, partita.getGiocatore());
        }

        return partita;
    }

    // =====================================================
    // 6. TURNO DEL GIOCATORE - STAND
    // =====================================================

    public Partita effettuaStand(Partita partita) {

        verificaPartitaInCorso(partita);

        Mano manoGiocatore = getManoGiocatore(partita);

        manoGiocatore.setStatoMano(statoMano.STATA);

        partita.setTurnoCorrente(Turno.DEALER);

        eseguiTurnoDealer(partita, "Standard17");

        determinaEsitoPartita(partita);

        salvaPartitaConclusa(partita, partita.getGiocatore());

        return partita;
    }

    // =====================================================
    // 7. TURNO DEL DEALER
    // =====================================================

    private void eseguiTurnoDealer(Partita partita, String tipoStrategia) {

        Mano manoDealer = partita.getDealer().getMano();

        scopriCarteDealer(manoDealer);

        calcolaPunteggio(manoDealer);

        dealerStrategy strategia =
                strategyFactory.getDealerStrategy(tipoStrategia);

        while (strategia.devePescare(
                manoDealer.getPunteggio(),
                haAssoSoft(manoDealer))) {

            Carta carta = pescaCarta(partita.getMazzo());

            aggiungiCarta(manoDealer, carta);

            int punteggio = calcolaPunteggio(manoDealer);

            if (punteggio > 21) {
                break;
            }
        }

        manoDealer.setStatoMano(
                manoDealer.getPunteggio() > 21
                        ? statoMano.SBALLATA
                        : statoMano.STATA
        );
    }

    // =====================================================
    // 8. DETERMINAZIONE ESITO PARTITA
    // =====================================================

    private void determinaEsitoPartita(Partita partita) {

        Mano manoGiocatore = getManoGiocatore(partita);
        Mano manoDealer = partita.getDealer().getMano();

        int punteggioGiocatore = manoGiocatore.getPunteggio();
        int punteggioDealer = manoDealer.getPunteggio();

        boolean giocatoreBust = punteggioGiocatore > 21;
        boolean dealerBust = punteggioDealer > 21;

        boolean blackjackGiocatore = haBlackjackNaturale(manoGiocatore);

        if (giocatoreBust) {

            partita.setEsito(esitoPartita.VITTORIA_DEALER);
        }
        else if (dealerBust || punteggioGiocatore > punteggioDealer) {

            partita.setEsito(esitoPartita.VITTORIA_GIOCATORE);

            accreditaVincita(partita, blackjackGiocatore);
        }
        else if (punteggioGiocatore < punteggioDealer) {

            partita.setEsito(esitoPartita.VITTORIA_DEALER);
        }
        else {

            partita.setEsito(esitoPartita.PAREGGIO);

            restituisciPuntata(partita);
        }

        partita.setStato(statoPartita.TERMINATA);

        popolaStatisticheFinali(partita);
    }

    // =====================================================
    // 9. BILANCIO DEL GIOCATORE
    // =====================================================

    private void accreditaVincita(Partita partita, boolean blackjackNaturale) {

        Giocatore giocatore = partita.getGiocatore();

        int puntata = partita.getImportoScommesso();

        int vincita;

        if (blackjackNaturale) {
            vincita = puntata + (int) (puntata * 1.5);
        }
        else {
            vincita = puntata * 2;
        }

        giocatore.setBilancioFiches(
                giocatore.getBilancioFiches() + vincita
        );
    }

    private void restituisciPuntata(Partita partita) {

        Giocatore giocatore = partita.getGiocatore();

        giocatore.setBilancioFiches(
                giocatore.getBilancioFiches() + partita.getImportoScommesso()
        );
    }

    // =====================================================
    // 10. STATISTICHE FINALI
    // =====================================================

    private void popolaStatisticheFinali(Partita partita) {

        Mano manoGiocatore = getManoGiocatore(partita);
        Mano manoDealer = partita.getDealer().getMano();

        partita.setDataOra(LocalDateTime.now());

        partita.setIsBlackjack(
        haBlackjackNaturale(manoGiocatore)
        );

        partita.setIsPlayerBust(
                manoGiocatore.getPunteggio() > 21
        );

        partita.setIsDealerBust(
                manoDealer.getPunteggio() > 21
        );

        // ------- TO DO --------- // 
        // partita.setIsDouble();
        //partita.setIsSplit();
        
    }

    // =====================================================
    // 11. PERSISTENZA
    // =====================================================

    private void salvaPartitaConclusa(Partita partita, Giocatore giocatore) {

        giocatoreDao.update(giocatore);
        partitaDao.save(partita);
    }

    // =====================================================
    // 12. CONTROLLI
    // =====================================================

    private void verificaPartitaInCorso(Partita partita) {

        if (partita == null) {
            throw new IllegalArgumentException("Partita non valida.");
        }

        if (partita.getStato() != statoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita non è in corso.");
        }
    }
}
