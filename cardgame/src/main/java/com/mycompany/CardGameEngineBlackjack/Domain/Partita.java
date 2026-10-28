package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy;

@Entity
@Table(name = "partita")
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partita")
    private Long id;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra;

    @Column(name = "importo_scommesso")
    private Integer importoScommesso;

    @Enumerated(EnumType.STRING)
    @Column(name = "esito", nullable = false)
    private esitoPartita esito;

    @ManyToOne
    @JoinColumn(name = "id_giocatore", nullable = false)
    private Giocatore giocatore;

    // --- NUovi campi persistenti per le Statistiche ---

    @Column(name = "is_blackjack")
    private Boolean isBlackjack = false;

    @Column(name = "is_player_bust")
    private Boolean isPlayerBust = false;

    @Column(name = "is_dealer_bust")
    private Boolean isDealerBust = false;

    @Column(name = "is_double")
    private Boolean isDouble = false;

    @Column(name = "is_split")
    private Boolean isSplit = false;

    // --- Campi di Dominio in RAM (Ignorati da Hibernate) ---

    @Transient 
    private statoPartita stato;

    @Transient 
    private Turno turnoCorrente;

    @Transient 
    private List<Mano> mani = new ArrayList<>();

    @Transient 
    private Mazzo mazzo;

    @Transient
    private Dealer dealer;

    public Partita() {
        this.dataOra = LocalDateTime.now();
    }

    public Partita(Long id, statoPartita stato, Turno turnoCorrente, esitoPartita esito, Giocatore giocatore, Dealer dealer, Mazzo mazzo) {
        this.id = id;
        this.dataOra = LocalDateTime.now();
        this.stato = stato;
        this.turnoCorrente = turnoCorrente;
        this.esito = esito;
        this.giocatore = giocatore;
        this.dealer = dealer;
        this.mazzo = mazzo;
    }

    // --- Getter e Setter Standard ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    public Integer getImportoScommesso() { return importoScommesso; }
    public void setImportoScommesso(Integer importoScommesso) { this.importoScommesso = importoScommesso; }

    public esitoPartita getEsito() { return esito; }
    public void setEsito(esitoPartita esito) { this.esito = esito; }

    public Giocatore getGiocatore() { return giocatore; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }

    public Boolean getIsBlackjack() { return isBlackjack; }
    public void setIsBlackjack(Boolean isBlackjack) { this.isBlackjack = isBlackjack; }

    public Boolean getIsPlayerBust() { return isPlayerBust; }
    public void setIsPlayerBust(Boolean isPlayerBust) { this.isPlayerBust = isPlayerBust; }

    public Boolean getIsDealerBust() { return isDealerBust; }
    public void setIsDealerBust(Boolean isDealerBust) { this.isDealerBust = isDealerBust; }

    public Boolean getIsDouble() { return isDouble; }
    public void setIsDouble(Boolean isDouble) { this.isDouble = isDouble; }

    public Boolean getIsSplit() { return isSplit; }
    public void setIsSplit(Boolean isSplit) { this.isSplit = isSplit; }

    public statoPartita getStato() { return stato; }
    public void setStato(statoPartita stato) { this.stato = stato; }

    public Turno getTurnoCorrente() { return turnoCorrente; }
    public void setTurnoCorrente(Turno turnoCorrente) { this.turnoCorrente = turnoCorrente; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public Mazzo getMazzo() { return mazzo; }
    public void setMazzo(Mazzo mazzo) { this.mazzo = mazzo; }

    public List<Mano> getMani() { return mani; }
    public void setMani(List<Mano> mani) { this.mani = mani; }

    // --- Logica di Gioco e Popolamento Statistiche ---

    public void distribuisciCarteIniziali() {
        if (mani.isEmpty() || dealer.getMano() == null) {
            throw new IllegalStateException("Mani non inizializzate.");
        }
        
        Mano manoGiocatore = getManoGiocatore();
        Mano manoDealer = dealer.getMano();

        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.calcolaPunteggio();

        manoDealer.aggiungiCarta(mazzo.pescaCarta());
        
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
        Mano manoGiocatore = getManoGiocatore(); 
        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.calcolaPunteggio();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.stato = statoPartita.TERMINATA;
            this.esito = esitoPartita.VITTORIA_DEALER;
            popolaStatisticheFinali();
        }
    }

    public void eseguiTurnoDealer(dealerStrategy strategy) {
        Mano manoDealer = dealer.getMano();
        
        for (Carta c : manoDealer.getCarte()) {
            c.setCoperta(false);
        }
        
        manoDealer.calcolaPunteggio();

        while (strategy.devePescare(manoDealer.getPunteggio(), manoDealer.haAssoSoft())) {
            manoDealer.aggiungiCarta(mazzo.pescaCarta());
            manoDealer.calcolaPunteggio();
            
            if (manoDealer.getStatoMano() == statoMano.SBALLATA) {
                break;
            }
        }
    }

    public void determinaEsitoPartita() {
        Mano manoGiocatore = getManoGiocatore();
        Mano manoDealer = dealer.getMano();
        
        int puntiGiocatore = manoGiocatore.getPunteggio();
        int puntiDealer = manoDealer.getPunteggio();
        
        int importoPuntata = manoGiocatore.getPuntata().getValoreTotale();
        this.importoScommesso = importoPuntata;

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else if (manoDealer.getStatoMano() == statoMano.SBALLATA || puntiGiocatore > puntiDealer) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
            int vincita = importoPuntata * 2;
            
            if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
                vincita = importoPuntata + (int)(importoPuntata * 1.5);
            }
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + vincita);
            
        } else if (puntiGiocatore < puntiDealer) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else {
            this.esito = esitoPartita.PAREGGIO;
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + importoPuntata);
        }
        
        popolaStatisticheFinali();
        this.stato = statoPartita.TERMINATA;
    }

    // Metodo di servizio per estrarre e congelare le metriche nei campi persistenti
    private void popolaStatisticheFinali() {
        this.dataOra = LocalDateTime.now();
        Mano manoGiocatore = getManoGiocatore();
        Mano manoDealer = dealer.getMano();

        if (manoGiocatore.getPuntata() != null) {
            this.importoScommesso = manoGiocatore.getPuntata().getValoreTotale();
        }

        this.isBlackjack = (manoGiocatore.getStatoMano() == statoMano.BLACKJACK);
        this.isPlayerBust = (manoGiocatore.getStatoMano() == statoMano.SBALLATA);
        this.isDealerBust = (manoDealer.getStatoMano() == statoMano.SBALLATA);
        
        // Se in futuro implementi i flag di Double/Split sulle mani o puntate, collegali qui:
        // this.isDouble = manoGiocatore.isDouble();
        // this.isSplit = manoGiocatore.isSplit();
    }

    public void inizializza(Giocatore giocatore, Integer importoPuntata) {
        this.giocatore = giocatore;
        this.dataOra = LocalDateTime.now();
        this.stato = statoPartita.IN_CORSO;
        this.turnoCorrente = Turno.GIOCATORE;
        this.esito = esitoPartita.IN_ATTESA;
        this.importoScommesso = importoPuntata;

        this.mazzo = new Mazzo(); 
        this.mazzo.mescola();

        this.mani = new ArrayList<>();
        Mano playerHand = new Mano();
        playerHand.setPartita(this);
        playerHand.setGiocatore(this.giocatore);
        playerHand.setStatoMano(statoMano.IN_GIOCO);
        this.mani.add(playerHand);

        Puntata puntata = new Puntata(); 
        puntata.setValoreTotale(importoPuntata);
        puntata.setMano(playerHand);
        playerHand.setPuntata(puntata);

        detraiFichesGiocatore(importoPuntata);

        this.dealer = new Dealer();
        Mano dealerHand = new Mano();
        dealerHand.setPartita(this);
        dealerHand.setDealer(this.dealer);
        dealerHand.setStatoMano(statoMano.IN_GIOCO);
        this.dealer.setMano(dealerHand);
    }

    private void detraiFichesGiocatore(Integer importo) {
        int bilancioAttuale = this.giocatore.getBilancioFiches();
        this.giocatore.setBilancioFiches(bilancioAttuale - importo);
    }

    public void aggiornaBilancioVincitore() {
        determinaEsitoPartita();
    }

    private Mano getManoGiocatore() {
        for (Mano m : this.mani) {
            if (m.getGiocatore() != null) {
                return m;
            }
        }
        throw new IllegalStateException("Mano del giocatore non trovata nella partita.");
    }
}