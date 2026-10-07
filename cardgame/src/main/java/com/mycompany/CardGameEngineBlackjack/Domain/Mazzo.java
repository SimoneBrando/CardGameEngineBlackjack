package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
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


    // Aggiungi fetch = FetchType.EAGER per caricare le carte insieme al mazzo
    @OneToMany(mappedBy = "mazzo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Carta> carte = new ArrayList<>();

    public Mazzo() {
        this.carte = new ArrayList<>();
        this.stato = statoMazzo.PRONTO;
        this.numeroMazzi = 1;
        
        // loop 52 volte indicato nel Sequence Diagram
        for (Seme seme : Seme.values()) {
            for (Rango rango : Rango.values()) {
                createCard(seme, rango);
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public statoMazzo getStato() { return stato; }
    public void setStato(statoMazzo stato) { this.stato = stato; }

    public Integer getNumeroMazzi() { return numeroMazzi; }
    public void setNumeroMazzi(Integer numeroMazzi) { this.numeroMazzi = numeroMazzi; }

    public List<Carta> getCarte() { return carte; }
    public void setCarte(List<Carta> carte) { this.carte = carte; }



    public Carta pescaCarta() {
        if (carte == null || carte.isEmpty()) {
            this.stato = statoMazzo.ESAURITO;
            throw new IllegalStateException("Il mazzo è esaurito!");
        }
        // Rimuove e restituisce l'ultima carta della lista
        return carte.remove(carte.size() - 1);
    }


    // Il metodo interno SD: Deck -> Deck : createCard(seme, valore)
    private void createCard(Seme seme, Rango rango) {
        List<Integer> valori;
        if (rango == Rango.ASSO) {
            valori = Arrays.asList(1, 11);
        } else if (rango == Rango.JACK || rango == Rango.REGINA || rango == Rango.RE) {
            valori = Arrays.asList(10);
        } else {
            valori = Arrays.asList(rango.ordinal() + 1); 
        }
        
        Carta carta = new Carta();
        carta.setSeme(seme);
        carta.setRango(rango);
        carta.setCoperta(false);
        carta.setValoriPossibili(valori);
        carta.setMazzo(this);
        
        this.carte.add(carta);
    }

    // SD: Game -> Deck : mescola()
    public void mescola() {
        java.util.Collections.shuffle(this.carte);
    }
}

