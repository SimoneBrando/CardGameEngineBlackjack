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
        Mano manoGiocatore = this.giocatore.getMani().get(0); // Assumendo che il giocatore abbia una sola mano
        Mano manoDealer = this.dealer.getMano();

        if (manoGiocatore.getPunteggio() > 21) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else if (manoDealer.getPunteggio() > 21 || manoGiocatore.getPunteggio() > manoDealer.getPunteggio()) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
        } else if (manoGiocatore.getPunteggio().equals(manoDealer.getPunteggio())) {
            this.esito = esitoPartita.PAREGGIO;
        } else {
            this.esito = esitoPartita.IN_ATTESA;
        }
    }
}