package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy;

@Entity
@Table(name = "partita")
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partita")
    private Long id;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra;

    @Column(name = "importo_scommesso")
    private Integer importoScommesso;

    @Enumerated(EnumType.STRING)
    @Column(name = "esito", nullable = false)
    private esitoPartita esito;

    @ManyToOne
    @JoinColumn(name = "id_giocatore", nullable = false)
    private Giocatore giocatore;

    // --- NUovi campi persistenti per le Statistiche ---

    @Column(name = "is_blackjack")
    private Boolean isBlackjack = false;

    @Column(name = "is_player_bust")
    private Boolean isPlayerBust = false;

    @Column(name = "is_dealer_bust")
    private Boolean isDealerBust = false;

    @Column(name = "is_double")
    private Boolean isDouble = false;

    @Column(name = "is_split")
    private Boolean isSplit = false;

    // --- Campi di Dominio in RAM (Ignorati da Hibernate) ---

    @Transient 
    private statoPartita stato;

    @Transient 
    private Turno turnoCorrente;

    @Transient 
    private List<Mano> mani = new ArrayList<>();

    @Transient 
    private Mazzo mazzo;

    @Transient
    private Dealer dealer;

    public Partita() {
        this.dataOra = LocalDateTime.now();
    }

    public Partita(Long id, statoPartita stato, Turno turnoCorrente, esitoPartita esito, Giocatore giocatore, Dealer dealer, Mazzo mazzo) {
        this.id = id;
        this.dataOra = LocalDateTime.now();
        this.stato = stato;
        this.turnoCorrente = turnoCorrente;
        this.esito = esito;
        this.giocatore = giocatore;
        this.dealer = dealer;
        this.mazzo = mazzo;
    }

    // --- Getter e Setter Standard ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    public Integer getImportoScommesso() { return importoScommesso; }
    public void setImportoScommesso(Integer importoScommesso) { this.importoScommesso = importoScommesso; }

    public esitoPartita getEsito() { return esito; }
    public void setEsito(esitoPartita esito) { this.esito = esito; }

    public Giocatore getGiocatore() { return giocatore; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }

    public Boolean getIsBlackjack() { return isBlackjack; }
    public void setIsBlackjack(Boolean isBlackjack) { this.isBlackjack = isBlackjack; }

    public Boolean getIsPlayerBust() { return isPlayerBust; }
    public void setIsPlayerBust(Boolean isPlayerBust) { this.isPlayerBust = isPlayerBust; }

    public Boolean getIsDealerBust() { return isDealerBust; }
    public void setIsDealerBust(Boolean isDealerBust) { this.isDealerBust = isDealerBust; }

    public Boolean getIsDouble() { return isDouble; }
    public void setIsDouble(Boolean isDouble) { this.isDouble = isDouble; }

    public Boolean getIsSplit() { return isSplit; }
    public void setIsSplit(Boolean isSplit) { this.isSplit = isSplit; }

    public statoPartita getStato() { return stato; }
    public void setStato(statoPartita stato) { this.stato = stato; }

    public Turno getTurnoCorrente() { return turnoCorrente; }
    public void setTurnoCorrente(Turno turnoCorrente) { this.turnoCorrente = turnoCorrente; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public Mazzo getMazzo() { return mazzo; }
    public void setMazzo(Mazzo mazzo) { this.mazzo = mazzo; }

    public List<Mano> getMani() { return mani; }
    public void setMani(List<Mano> mani) { this.mani = mani; }

    
}