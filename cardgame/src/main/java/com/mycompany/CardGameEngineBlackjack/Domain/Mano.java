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




    public void aggiungiCarta(Carta carta) {
        this.carte.add(carta);
    }


    public int calcolaPunteggio() {
        int totale = 0;
        int assi = 0;

        for (Carta c : carte) {
            // L'Enum Rango va importato: com.mycompany.blackjack.domain.enums.Rango
            if (c.getRango() == Rango.ASSO) {
                assi++;
                totale += 11; 
            } else {
                totale += c.getValoriPossibili().get(0);
            }
        }

        // Declassa gli assi se il punteggio sballa
        while (totale > 21 && assi > 0) {
            totale -= 10;
            assi--;
        }

        this.punteggio = totale;
        
        if (this.punteggio > 21) {
            this.statoMano = statoMano.SBALLATA;
        } else if (this.punteggio == 21 && carte.size() == 2) {
            this.statoMano = statoMano.BLACKJACK;
        }
        
        return totale;
    }


    public boolean haAssoSoft() {
        // Un Asso è "soft" se vale 11 e non fa sballare la mano
        int punteggioBase = 0;
        boolean haAsso = false;
        for (Carta c : carte) {
            if (c.getRango() == Rango.ASSO) haAsso = true;
            else punteggioBase += c.getValoriPossibili().get(0);
        }
        return haAsso && (punteggioBase + 11 <= 21);
    }

    public void scopriCartaCoperta() {
        for (Carta c : this.carte) {
            if (c.getCoperta()) {
                c.setCoperta(false);
            }
        }
    }
}