package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy;

@Entity
@Table(name = "partite")
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partita")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private statoPartita stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno_corrente", nullable = false)
    private Turno turnoCorrente;

    @Enumerated(EnumType.STRING)
    @Column(name = "esito", nullable = false)
    private esitoPartita esito;

    @ManyToOne
    @JoinColumn(name = "id_giocatore", nullable = false)
    private Giocatore giocatore;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_dealer", nullable = false)
    private Dealer dealer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_mazzo", nullable = false)
    private Mazzo mazzo;

    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL)
    private List<Mano> mani = new ArrayList<>();

    public Partita() {}

    public Partita(Long id, statoPartita stato, Turno turnoCorrente, esitoPartita esito, Giocatore giocatore, Dealer dealer, Mazzo mazzo) {
        this.id = id;
        this.stato = stato;
        this.turnoCorrente = turnoCorrente;
        this.esito = esito;
        this.giocatore = giocatore;
        this.dealer = dealer;
        this.mazzo = mazzo;
    }

    // --- Getter e Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public statoPartita getStato() { return stato; }
    public void setStato(statoPartita stato) { this.stato = stato; }

    public Turno getTurnoCorrente() { return turnoCorrente; }
    public void setTurnoCorrente(Turno turnoCorrente) { this.turnoCorrente = turnoCorrente; }

    public esitoPartita getEsito() { return esito; }
    public void setEsito(esitoPartita esito) { this.esito = esito; }

    public Giocatore getGiocatore() { return giocatore; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public Mazzo getMazzo() { return mazzo; }
    public void setMazzo(Mazzo mazzo) { this.mazzo = mazzo; }

    public List<Mano> getMani() { return mani; }
    public void setMani(List<Mano> mani) { this.mani = mani; }

    public void distribuisciCarteIniziali() {
        if (mani.isEmpty() || dealer.getMano() == null) {
            throw new IllegalStateException("Mani non inizializzate.");
        }
        
        Mano manoGiocatore = mani.get(0);
        Mano manoDealer = dealer.getMano();

        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.calcolaPunteggio();

        manoDealer.aggiungiCarta(mazzo.pescaCarta());
        
        // La seconda carta del dealer è inizialmente coperta
        Carta cartaCoperta = mazzo.pescaCarta();
        cartaCoperta.setCoperta(true);
        manoDealer.aggiungiCarta(cartaCoperta);
        manoDealer.calcolaPunteggio();

        if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
            this.stato = statoPartita.TERMINATA;
            determinaEsitoPartita();
        }
    }

    public void eseguiHitGiocatore() {
        // Per ora gestiamo la singola mano (no split)
        Mano manoGiocatore = mani.get(0); 
        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.calcolaPunteggio();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.stato = statoPartita.TERMINATA;
            this.esito = esitoPartita.VITTORIA_DEALER;
            // Il giocatore perde la puntata: non aggiungiamo nulla al bilancio
        }
    }

    public void eseguiTurnoDealer(dealerStrategy strategy) {
        Mano manoDealer = dealer.getMano();
        
        // Scopri la carta nascosta
        for (Carta c : manoDealer.getCarte()) {
            c.setCoperta(false);
        }
        
        manoDealer.calcolaPunteggio();

        // Finché la strategia impone di pescare, il banco estrae carte
        while (strategy.devePescare(manoDealer.getPunteggio(), manoDealer.haAssoSoft())) {
            manoDealer.aggiungiCarta(mazzo.pescaCarta());
            manoDealer.calcolaPunteggio();
            
            if (manoDealer.getStatoMano() == statoMano.SBALLATA) {
                break;
            }
        }
    }

    public void determinaEsitoPartita() {
        Mano manoGiocatore = mani.get(0);
        Mano manoDealer = dealer.getMano();
        
        int puntiGiocatore = manoGiocatore.getPunteggio();
        int puntiDealer = manoDealer.getPunteggio();
        
        // Ottieni l'importo della puntata effettuata
        int importoPuntata = manoGiocatore.getPuntata().getValoreTotale();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else if (manoDealer.getStatoMano() == statoMano.SBALLATA) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + (importoPuntata * 2));
        } else if (puntiGiocatore > puntiDealer) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
            // Vittoria con Blackjack paga 3:2, vittoria normale 1:1 (2x puntata in totale)
            if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
                giocatore.setBilancioFiches(giocatore.getBilancioFiches() + (int)(importoPuntata * 2.5));
            } else {
                giocatore.setBilancioFiches(giocatore.getBilancioFiches() + (importoPuntata * 2));
            }
        } else if (puntiGiocatore < puntiDealer) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else {
            this.esito = esitoPartita.PAREGGIO;
            // Restituisce la puntata originale
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + importoPuntata);
        }
        
        this.stato = statoPartita.TERMINATA;
    }

    
    // Da aggiungere in com.mycompany.CardGameEngineBlackjack.Domain.Partita

    public void inizializza(Giocatore giocatore, Integer importoPuntata) {
        this.giocatore = giocatore;
        this.stato = statoPartita.IN_CORSO;
        this.turnoCorrente = Turno.GIOCATORE;
        this.esito = esitoPartita.IN_ATTESA;

        // SD: Game -> Deck : new()
        this.mazzo = new Mazzo(); 
        
        // SD: Game -> Deck : mescola()
        this.mazzo.mescola();

        // SD: Game -> PlayerHand : new()
        this.mani = new ArrayList<>();
        Mano playerHand = new Mano();
        playerHand.setPartita(this);
        playerHand.setGiocatore(this.giocatore);
        playerHand.setStatoMano(statoMano.IN_GIOCO);
        this.mani.add(playerHand);

        // SD: Game -> Bet : new(importo, mg)
        Puntata puntata = new Puntata(); 
        puntata.setValoreTotale(importoPuntata);
        puntata.setMano(playerHand);
        playerHand.setPuntata(puntata);

        // SD: Game -> Game : detraiFichesGiocatore(importo)
        this.detraiFichesGiocatore(importoPuntata);

        // SD: Game -> DealerHand : new()
        this.dealer = new Dealer();
        Mano dealerHand = new Mano();
        dealerHand.setPartita(this);
        dealerHand.setDealer(this.dealer);
        dealerHand.setStatoMano(statoMano.IN_GIOCO);
        this.dealer.setMano(dealerHand);
    }

    // Metodo interno indicato nel diagramma per coerenza finanziaria
    private void detraiFichesGiocatore(Integer importo) {
        int bilancioAttuale = this.giocatore.getBilancioFiches();
        this.giocatore.setBilancioFiches(bilancioAttuale - importo);
    }


    public void aggiornaBilancioVincitore() {
        Mano manoGiocatore = mani.get(0);
        Mano manoDealer = dealer.getMano();
        
        int puntiGiocatore = manoGiocatore.getPunteggio();
        int puntiDealer = manoDealer.getPunteggio();
        int importoPuntata = manoGiocatore.getPuntata().getValoreTotale();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.esito = esitoPartita.VITTORIA_DEALER;
            // Il bilancio è già stato decurtato all'inizializzazione, non facciamo nulla.
        } else if (manoDealer.getStatoMano() == statoMano.SBALLATA || puntiGiocatore > puntiDealer) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
            // Paga 1:1 (restituisce la puntata + vincita equivalente)
            int vincita = importoPuntata * 2;
            
            if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
                // Paga 3:2 per il Blackjack naturale
                vincita = importoPuntata + (int)(importoPuntata * 1.5);
            }
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + vincita);
            
        } else if (puntiGiocatore < puntiDealer) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else {
            this.esito = esitoPartita.PAREGGIO;
            // Restituisce la puntata
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + importoPuntata);
        }
        
        this.stato = statoPartita.TERMINATA;
    }

        
        
}