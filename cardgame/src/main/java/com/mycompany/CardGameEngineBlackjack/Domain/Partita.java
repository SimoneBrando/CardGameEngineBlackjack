package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.CardGameEngineBlackjack.Application.Strategy.dealerStrategy;

@Entity
@Table(name = "partite")
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partita")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private statoPartita stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno_corrente", nullable = false)
    private Turno turnoCorrente;

    @Enumerated(EnumType.STRING)
    @Column(name = "esito", nullable = false)
    private esitoPartita esito;

    @ManyToOne
    @JoinColumn(name = "id_giocatore", nullable = false)
    private Giocatore giocatore;

    /* 
     * FETCH TYPE EAGER vs LAZY:
     * Di default, le relazioni @OneToMany sono di tipo LAZY (pigre). Questo significa che 
     * Hibernate carica dal DB solo i dati della Partita, inserendo al posto della lista 
     * un "Proxy" vuoto per risparmiare memoria. Quando il codice chiama mani.get(0), 
     * il Proxy tenta di fare una nuova query al DB per scaricare i dati.
     * Tuttavia, poiché il nostro GenericDao usa il costrutto "try-with-resources", 
     * la Sessione del database viene chiusa un istante dopo il findById(). 
     * Trovando la connessione chiusa, il Proxy va in crash (LazyInitializationException).
     * 
     * Impostando fetch = FetchType.EAGER (avido), costringiamo Hibernate a fare una 
     * query con una JOIN immediata. In questo modo scarica l'intero blocco di dati 
     * (Partita + Mani) in un colpo solo, caricandolo nella RAM mentre la Sessione 
     * è ancora aperta.
     */
    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Mano> mani = new ArrayList<>();

    // Le relazioni @OneToOne sono già EAGER di default, ma è buona norma esplicitarlo
    // per chiarezza architetturale se si vuole il caricamento immediato.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mazzo", nullable = false)
    private Mazzo mazzo;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_dealer", nullable = false)
    private Dealer dealer;

    public Partita() {}

    public Partita(Long id, statoPartita stato, Turno turnoCorrente, esitoPartita esito, Giocatore giocatore, Dealer dealer, Mazzo mazzo) {
        this.id = id;
        this.stato = stato;
        this.turnoCorrente = turnoCorrente;
        this.esito = esito;
        this.giocatore = giocatore;
        this.dealer = dealer;
        this.mazzo = mazzo;
    }

    // --- Getter e Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public statoPartita getStato() { return stato; }
    public void setStato(statoPartita stato) { this.stato = stato; }

    public Turno getTurnoCorrente() { return turnoCorrente; }
    public void setTurnoCorrente(Turno turnoCorrente) { this.turnoCorrente = turnoCorrente; }

    public esitoPartita getEsito() { return esito; }
    public void setEsito(esitoPartita esito) { this.esito = esito; }

    public Giocatore getGiocatore() { return giocatore; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public Mazzo getMazzo() { return mazzo; }
    public void setMazzo(Mazzo mazzo) { this.mazzo = mazzo; }

    public List<Mano> getMani() { return mani; }
    public void setMani(List<Mano> mani) { this.mani = mani; }

    public void distribuisciCarteIniziali() {
        if (mani.isEmpty() || dealer.getMano() == null) {
            throw new IllegalStateException("Mani non inizializzate.");
        }
        
        // Usa il nuovo metodo invece di mani.get(0)
        Mano manoGiocatore = getManoGiocatore();
        Mano manoDealer = dealer.getMano();

        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.calcolaPunteggio();

        manoDealer.aggiungiCarta(mazzo.pescaCarta());
        
        Carta cartaCoperta = mazzo.pescaCarta();
        cartaCoperta.setCoperta(true);
        manoDealer.aggiungiCarta(cartaCoperta);
        manoDealer.calcolaPunteggio();

        if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
            this.stato = statoPartita.TERMINATA;
            determinaEsitoPartita();
        }
    }

    public void eseguiHitGiocatore() {
        // Usa il nuovo metodo
        Mano manoGiocatore = getManoGiocatore(); 
        manoGiocatore.aggiungiCarta(mazzo.pescaCarta());
        manoGiocatore.calcolaPunteggio();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.stato = statoPartita.TERMINATA;
            this.esito = esitoPartita.VITTORIA_DEALER;
        }
    }

    public void eseguiTurnoDealer(dealerStrategy strategy) {
        Mano manoDealer = dealer.getMano();
        
        // Scopri la carta nascosta
        for (Carta c : manoDealer.getCarte()) {
            c.setCoperta(false);
        }
        
        manoDealer.calcolaPunteggio();

        // Finché la strategia impone di pescare, il banco estrae carte
        while (strategy.devePescare(manoDealer.getPunteggio(), manoDealer.haAssoSoft())) {
            manoDealer.aggiungiCarta(mazzo.pescaCarta());
            manoDealer.calcolaPunteggio();
            
            if (manoDealer.getStatoMano() == statoMano.SBALLATA) {
                break;
            }
        }
    }

   public void determinaEsitoPartita() {
        // Usa il nuovo metodo
        Mano manoGiocatore = getManoGiocatore();
        Mano manoDealer = dealer.getMano();
        
        int puntiGiocatore = manoGiocatore.getPunteggio();
        int puntiDealer = manoDealer.getPunteggio();
        
        int importoPuntata = manoGiocatore.getPuntata().getValoreTotale();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else if (manoDealer.getStatoMano() == statoMano.SBALLATA || puntiGiocatore > puntiDealer) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
            int vincita = importoPuntata * 2;
            
            if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
                vincita = importoPuntata + (int)(importoPuntata * 1.5);
            }
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + vincita);
            
        } else if (puntiGiocatore < puntiDealer) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else {
            this.esito = esitoPartita.PAREGGIO;
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + importoPuntata);
        }
        
        this.stato = statoPartita.TERMINATA;
    }
    

    
    // Da aggiungere in com.mycompany.CardGameEngineBlackjack.Domain.Partita

    public void inizializza(Giocatore giocatore, Integer importoPuntata) {
        this.giocatore = giocatore;
        this.stato = statoPartita.IN_CORSO;
        this.turnoCorrente = Turno.GIOCATORE;
        this.esito = esitoPartita.IN_ATTESA;

        // SD: Game -> Deck : new()
        this.mazzo = new Mazzo(); 
        
        // SD: Game -> Deck : mescola()
        this.mazzo.mescola();

        // SD: Game -> PlayerHand : new()
        this.mani = new ArrayList<>();
        Mano playerHand = new Mano();
        playerHand.setPartita(this);
        playerHand.setGiocatore(this.giocatore);
        playerHand.setStatoMano(statoMano.IN_GIOCO);
        this.mani.add(playerHand);

        // SD: Game -> Bet : new(importo, mg)
        Puntata puntata = new Puntata(); 
        puntata.setValoreTotale(importoPuntata);
        puntata.setMano(playerHand);
        playerHand.setPuntata(puntata);

        // SD: Game -> Game : detraiFichesGiocatore(importo)
        this.detraiFichesGiocatore(importoPuntata);

        // SD: Game -> DealerHand : new()
        this.dealer = new Dealer();
        Mano dealerHand = new Mano();
        dealerHand.setPartita(this);
        dealerHand.setDealer(this.dealer);
        dealerHand.setStatoMano(statoMano.IN_GIOCO);
        this.dealer.setMano(dealerHand);
    }

    // Metodo interno indicato nel diagramma per coerenza finanziaria
    private void detraiFichesGiocatore(Integer importo) {
        int bilancioAttuale = this.giocatore.getBilancioFiches();
        this.giocatore.setBilancioFiches(bilancioAttuale - importo);
    }


    public void aggiornaBilancioVincitore() {
        Mano manoGiocatore = mani.get(0);
        Mano manoDealer = dealer.getMano();
        
        int puntiGiocatore = manoGiocatore.getPunteggio();
        int puntiDealer = manoDealer.getPunteggio();
        int importoPuntata = manoGiocatore.getPuntata().getValoreTotale();

        if (manoGiocatore.getStatoMano() == statoMano.SBALLATA) {
            this.esito = esitoPartita.VITTORIA_DEALER;
            // Il bilancio è già stato decurtato all'inizializzazione, non facciamo nulla.
        } else if (manoDealer.getStatoMano() == statoMano.SBALLATA || puntiGiocatore > puntiDealer) {
            this.esito = esitoPartita.VITTORIA_GIOCATORE;
            // Paga 1:1 (restituisce la puntata + vincita equivalente)
            int vincita = importoPuntata * 2;
            
            if (manoGiocatore.getStatoMano() == statoMano.BLACKJACK) {
                // Paga 3:2 per il Blackjack naturale
                vincita = importoPuntata + (int)(importoPuntata * 1.5);
            }
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + vincita);
            
        } else if (puntiGiocatore < puntiDealer) {
            this.esito = esitoPartita.VITTORIA_DEALER;
        } else {
            this.esito = esitoPartita.PAREGGIO;
            // Restituisce la puntata
            giocatore.setBilancioFiches(giocatore.getBilancioFiches() + importoPuntata);
        }
        
        this.stato = statoPartita.TERMINATA;
    }


    private Mano getManoGiocatore() {
        for (Mano m : this.mani) {
            // La mano del giocatore è l'unica ad avere l'entità Giocatore valorizzata
            if (m.getGiocatore() != null) {
                return m;
            }
        }
        throw new IllegalStateException("Mano del giocatore non trovata nella partita.");
    }

        
        
}