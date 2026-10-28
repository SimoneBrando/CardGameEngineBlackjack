package com.mycompany.CardGameEngineBlackjack.Domain;

import jakarta.persistence.*;

public class Fiche {

    private int valore;
    
    @Transient
    private Puntata puntata;

    public Fiche() {}

    public Fiche(Integer valore, Puntata puntata) {
        this.valore = valore;
        this.puntata = puntata;
    }

    public Integer getValore() { return valore; }
    public void setValore(Integer valore) { this.valore = valore; }


    public Puntata getPuntata() { return puntata; }
    public void setPuntata(Puntata puntata) { this.puntata = puntata; }
}

