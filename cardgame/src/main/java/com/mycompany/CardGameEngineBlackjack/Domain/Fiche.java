package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;

@Entity
@Table(name = "fiches")
public class Fiche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fiche")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "taglio", nullable = false)
    private taglioFiche taglio;

    @Column(name = "quantita", nullable = false)
    private Integer quantita;

    @ManyToOne
    @JoinColumn(name = "id_puntata", nullable = false)
    private Puntata puntata;

    public Fiche() {}

    public Fiche(Long id, taglioFiche taglio, Integer quantita, Puntata puntata) {
        this.id = id;
        this.taglio = taglio;
        this.quantita = quantita;
        this.puntata = puntata;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public taglioFiche getTaglio() { return taglio; }
    public void setTaglio(taglioFiche taglio) { this.taglio = taglio; }

    public Integer getQuantita() { return quantita; }
    public void setQuantita(Integer quantita) { this.quantita = quantita; }

    public Puntata getPuntata() { return puntata; }
    public void setPuntata(Puntata puntata) { this.puntata = puntata; }
}

