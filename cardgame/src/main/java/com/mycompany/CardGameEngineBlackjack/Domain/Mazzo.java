package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import java.util.ArrayList;
import java.util.List;


public class Mazzo {

    private statoMazzo stato;
    private Integer numeroMazzi;
    private List<Carta> carte = new ArrayList<>();

    public Mazzo() {
        this.carte = new ArrayList<>();
        this.stato = statoMazzo.PRONTO;
        this.numeroMazzi = 1;
    }

    public statoMazzo getStato() { return stato; }
    public void setStato(statoMazzo stato) { this.stato = stato; }

    public Integer getNumeroMazzi() { return numeroMazzi; }
    public void setNumeroMazzi(Integer numeroMazzi) { this.numeroMazzi = numeroMazzi; }

    public List<Carta> getCarte() { return carte; }
    public void setCarte(List<Carta> carte) { this.carte = carte; }

    
}

