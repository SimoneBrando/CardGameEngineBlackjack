package com.mycompany.CardGameEngineBlackjack.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dealers")
public class Dealer {

    private Mano mano;

    public Dealer() {}

    public Mano getMano() { return mano; }
    public void setMano(Mano mano) { this.mano = mano; }
}