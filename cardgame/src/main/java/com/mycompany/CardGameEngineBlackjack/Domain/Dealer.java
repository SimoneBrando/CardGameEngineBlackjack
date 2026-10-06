package com.mycompany.CardGameEngineBlackjack.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dealers")
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dealer")
    private Long id;

    // Il dealer ha generalmente una sola mano alla volta
    @OneToOne(mappedBy = "dealer", cascade = CascadeType.ALL)
    private Mano mano;

    public Dealer() {}

    public Dealer(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mano getMano() { return mano; }
    public void setMano(Mano mano) { this.mano = mano; }
}