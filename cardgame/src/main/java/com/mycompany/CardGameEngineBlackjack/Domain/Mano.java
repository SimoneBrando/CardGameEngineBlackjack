package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


public class Mano {

    private Integer punteggio;
    private statoMano statoMano;
    private Boolean derivataDaSplit;
    private Partita partita;
    private Giocatore giocatore;
    private Dealer dealer;
    private List<Carta> carte = new ArrayList<>();

    @Transient
    private Puntata puntata;

    public Mano() {}

    public Mano(Integer punteggio, statoMano statoMano, Boolean derivataDaSplit, Partita partita) {
        this.punteggio = punteggio;
        this.statoMano = statoMano;
        this.derivataDaSplit = derivataDaSplit;
        this.partita = partita;
    }

    // --- Getter e Setter ---
    public Integer getPunteggio() { return punteggio; }
    public void setPunteggio(Integer punteggio) { this.punteggio = punteggio; }

    public statoMano getStatoMano() { return statoMano; }
    public void setStatoMano(statoMano statoMano) { this.statoMano = statoMano; }

    public Boolean getDerivataDaSplit() { return derivataDaSplit; }
    public void setDerivataDaSplit(Boolean derivataDaSplit) { this.derivataDaSplit = derivataDaSplit; }

    public Partita getPartita() { return partita; }
    public void setPartita(Partita partita) { this.partita = partita; }

    public Giocatore getGiocatore() { return giocatore; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public List<Carta> getCarte() { return carte; }
    public void setCarte(List<Carta> carte) { this.carte = carte; }

    public Puntata getPuntata() { return puntata; }
    public void setPuntata(Puntata puntata) { this.puntata = puntata; }

}