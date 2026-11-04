package com.mycompany.CardGameEngineBlackjack.Domain;

import java.util.ArrayList;
import java.util.List;


public class Puntata {

    private Integer valoreTotale;
    private Mano mano;

    public Puntata() {}

    public Puntata(Integer valoreTotale, Mano mano) {
        this.valoreTotale = valoreTotale;
        this.mano = mano;
    }

    public Integer getValoreTotale() { return valoreTotale; }
    public void setValoreTotale(Integer valoreTotale) { this.valoreTotale = valoreTotale; }

    public Mano getMano() { return mano; }
    public void setMano(Mano mano) { this.mano = mano; }

}

