package com.mycompany.CardGameEngineBlackjack.Domain;

import java.util.ArrayList;
import java.util.List;


public class Puntata {

    private Integer valoreTotale;
    private Mano mano;
    private List<Fiche> fiches = new ArrayList<>();

    public Puntata() {}

    public Puntata(Integer valoreTotale, Mano mano) {
        this.valoreTotale = valoreTotale;
        this.mano = mano;
    }

    public Integer getValoreTotale() { return valoreTotale; }
    public void setValoreTotale(Integer valoreTotale) { this.valoreTotale = valoreTotale; }

    public Mano getMano() { return mano; }
    public void setMano(Mano mano) { this.mano = mano; }

    public List<Fiche> getFiches() { return fiches; }
    public void setFiches(List<Fiche> fiches) { this.fiches = fiches; }
}

