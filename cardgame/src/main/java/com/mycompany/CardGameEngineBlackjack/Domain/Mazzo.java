package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mazzi")
public class Mazzo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mazzo")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private statoMazzo stato;

    @Column(name = "numero_mazzi", nullable = false)
    private Integer numeroMazzi;

    @OneToMany(mappedBy = "mazzo", cascade = CascadeType.ALL)
    private List<Carta> carte = new ArrayList<>();

    public Mazzo() {}

    public Mazzo(Long id, statoMazzo stato, Integer numeroMazzi) {
        this.id = id;
        this.stato = stato;
        this.numeroMazzi = numeroMazzi;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public statoMazzo getStato() { return stato; }
    public void setStato(statoMazzo stato) { this.stato = stato; }

    public Integer getNumeroMazzi() { return numeroMazzi; }
    public void setNumeroMazzi(Integer numeroMazzi) { this.numeroMazzi = numeroMazzi; }

    public List<Carta> getCarte() { return carte; }
    public void setCarte(List<Carta> carte) { this.carte = carte; }
}

