package com.mycompany.CardGameEngineBlackjack.Domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "puntate")
public class Puntata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puntata")
    private Long id;

    @Column(name = "valore_totale", nullable = false)
    private Integer valoreTotale;

    @OneToOne
    @JoinColumn(name = "id_mano", nullable = false)
    private Mano mano;

    @OneToMany(mappedBy = "puntata", cascade = CascadeType.ALL)
    private List<Fiche> fiches = new ArrayList<>();

    public Puntata() {}

    public Puntata(Long id, Integer valoreTotale, Mano mano) {
        this.id = id;
        this.valoreTotale = valoreTotale;
        this.mano = mano;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getValoreTotale() { return valoreTotale; }
    public void setValoreTotale(Integer valoreTotale) { this.valoreTotale = valoreTotale; }

    public Mano getMano() { return mano; }
    public void setMano(Mano mano) { this.mano = mano; }

    public List<Fiche> getFiches() { return fiches; }
    public void setFiches(List<Fiche> fiches) { this.fiches = fiches; }
}

