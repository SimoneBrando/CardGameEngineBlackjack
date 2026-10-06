package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mani")
public class Mano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mano")
    private Long id;

    @Column(name = "punteggio")
    private Integer punteggio;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_mano")
    private statoMano statoMano;

    @Column(name = "derivata_da_split")
    private Boolean derivataDaSplit;

    @ManyToOne
    @JoinColumn(name = "id_partita", nullable = false)
    private Partita partita;

    @ManyToOne
    @JoinColumn(name = "id_giocatore") // Nullable perché potrebbe essere del Dealer
    private Giocatore giocatore;

    @OneToOne
    @JoinColumn(name = "id_dealer") // Nullable perché potrebbe essere del Giocatore
    private Dealer dealer;

    @ManyToMany
    @JoinTable(
        name = "mano_carta",
        joinColumns = @JoinColumn(name = "id_mano"),
        inverseJoinColumns = @JoinColumn(name = "id_carta")
    )
    private List<Carta> carte = new ArrayList<>();

    @OneToOne(mappedBy = "mano", cascade = CascadeType.ALL)
    private Puntata puntata;

    public Mano() {}

    public Mano(Long id, Integer punteggio, statoMano statoMano, Boolean derivataDaSplit, Partita partita) {
        this.id = id;
        this.punteggio = punteggio;
        this.statoMano = statoMano;
        this.derivataDaSplit = derivataDaSplit;
        this.partita = partita;
    }

    // --- Getter e Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getPunteggio() { return punteggio; }
    public void setPunteggio(Integer punteggio) { this.punteggio = punteggio; }

    public statoMano getStatoMano() { return statoMano; }
    public void setStatoMano(statoMano statoMano) { this.statoMano = statoMano; }

    public Boolean getDerivataDaSplit() { return derivataDaSplit; }
    public void setDerivataDaSplit(Boolean derivataDaSplit) { this.derivataDaSplit = derivataDaSplit; }

    public Partita getPartita() { return partita; }
    public void setPartita(Partita partita) { this.partita = partita; }

    public Giocatore getGiocatore() { return giocatore; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public List<Carta> getCarte() { return carte; }
    public void setCarte(List<Carta> carte) { this.carte = carte; }

    public Puntata getPuntata() { return puntata; }
    public void setPuntata(Puntata puntata) { this.puntata = puntata; }
}