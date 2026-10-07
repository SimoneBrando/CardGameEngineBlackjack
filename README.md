# CardGame-Engine---Blackjack

Applicazione Java per la simulazione del gioco del **Blackjack**, sviluppata nell'ambito del progetto **CardGame Engine** da **LudosGames**.

Il progetto implementa il motore di gioco, la gestione delle partite, delle mani, delle carte e del dealer, con persistenza dei dati tramite JPA/Hibernate e un'interfaccia grafica realizzata con JavaFX.

---

## 👥 Autori

* **Simone Brandolini**
* **Francesco Di Giacomo**
* **Stefano Giuliani**

**Team:** LudosGames
**Versione:** 1.0
**Data documento Use Case:** 11/11/2025

---

## 🎯 Obiettivo del progetto

L'obiettivo di **CardGame Engine - Blackjack** è realizzare un'applicazione in grado di gestire una partita di Blackjack attraverso un'architettura software modulare e facilmente estendibile.

Il sistema gestisce:

* creazione e inizializzazione di una partita;
* gestione del giocatore;
* gestione del dealer;
* creazione e mescolamento del mazzo;
* distribuzione delle carte;
* calcolo del punteggio;
* gestione delle carte coperte;
* azioni del giocatore;
* turno automatico del dealer;
* determinazione dell'esito della partita;
* gestione delle fiches e delle puntate;
* persistenza dei dati tramite database.

---

## 🃏 Blackjack

Il gioco segue le principali regole del Blackjack.

### Valore delle carte

* Carte numeriche → valore nominale;
* Jack, Queen e King → 10;
* Asso → 1 oppure 11, in base al punteggio della mano.

### Blackjack

Un **Blackjack naturale** si verifica quando il giocatore ottiene 21 con le prime due carte.

Il Blackjack viene distinto da un normale punteggio di 21 ottenuto con più di due carte.

### Turno iniziale

All'inizio della partita:

1. viene creato il mazzo;
2. il mazzo viene mescolato;
3. vengono create la mano del giocatore e quella del dealer;
4. vengono distribuite due carte al giocatore;
5. vengono distribuite due carte al dealer;
6. una delle carte del dealer rimane coperta;
7. il turno passa al giocatore.

---

## 🎮 Azioni del giocatore

Il sistema prevede le principali azioni del Blackjack:

| Azione     | Descrizione                             |
| ---------- | --------------------------------------- |
| **Hit**    | Il giocatore pesca una nuova carta      |
| **Stand**  | Il giocatore termina il proprio turno   |
| **Double** | Raddoppia la puntata e riceve una carta |
| **Split**  | Divide una coppia in due mani           |

> **Nota:** nell'implementazione corrente sono operative principalmente le azioni **Hit** e **Stand**. Double e Split sono previste dal modello degli Use Case e potranno essere implementate successivamente.

---

## 🤖 Dealer

Il comportamento del dealer è gestito attraverso una strategia dedicata.

Attualmente è disponibile la strategia:

```text
Standard17
```

che prevede:

* Hit con punteggio inferiore a 17;
* Stand con punteggio pari o superiore a 17.

Il progetto utilizza una **Strategy Factory**, che permette di estendere il comportamento del dealer introducendo nuove strategie senza modificare direttamente il codice della partita.

È inoltre prevista la strategia:

```text
HitSoft17
```

che consente al dealer di pescare anche con Soft 17.

---

## 🏆 Esito della partita

Al termine del turno del dealer il sistema confronta i punteggi del giocatore e del dealer.

Gli esiti possibili sono:

* **VITTORIA_GIOCATORE**
* **VITTORIA_DEALER**
* **PAREGGIO**

Sono inoltre gestiti:

* Blackjack;
* Bust del giocatore;
* Bust del dealer;
* Push;
* pagamento della puntata;
* pagamento del Blackjack naturale.

Il Blackjack naturale prevede un pagamento **3:2**, mentre una vittoria normale prevede un pagamento **1:1**.

---

## 💰 Gestione delle fiches

Ogni partita è associata a un giocatore e a una puntata.

All'avvio della partita:

```text
Saldo giocatore
       ↓
Detrazione puntata
       ↓
Inizio partita
```

Al termine:

```text
Vittoria      → pagamento della vincita
Blackjack     → pagamento 3:2
Pareggio      → restituzione della puntata
Sconfitta     → nessuna restituzione
```

---

# 🏗️ Architettura

Il progetto segue una struttura a livelli separando presentazione, logica applicativa, dominio e persistenza.

```text
┌──────────────────────────────┐
│             UI               │
│        JavaFX / View         │
└──────────────┬───────────────┘
               │
               ↓
┌──────────────────────────────┐
│         Application          │
│      Game Controller         │
└──────────────┬───────────────┘
               │
               ↓
┌──────────────────────────────┐
│           Domain             │
│ Partita / Mano / Carta / ... │
└──────────────┬───────────────┘
               │
               ↓
┌──────────────────────────────┐
│      TechnicalService        │
│       DAO / Persistence      │
└──────────────────────────────┘
```

L'interfaccia grafica non contiene le regole del Blackjack.

La UI comunica con il livello applicativo attraverso:

```java
blackjackGameController
```

che coordina le operazioni sulla partita.

---

# 🧩 Principali classi

## `Partita`

Rappresenta la partita di Blackjack e coordina il comportamento principale del dominio.

Gestisce:

* inizializzazione della partita;
* distribuzione delle carte;
* Hit del giocatore;
* turno del dealer;
* determinazione dell'esito;
* gestione delle fiches.

Metodi principali:

```java
avviaNuovaPartita(...)
```

tramite il controller applicativo:

```java
effettuaHit(...)
effettuaStand(...)
```

---

## `Mano`

Rappresenta una mano di gioco.

Gestisce:

* carte presenti nella mano;
* punteggio;
* stato della mano;
* puntata;
* calcolo del punteggio;
* riconoscimento del Blackjack;
* riconoscimento del Bust;
* gestione dell'Asso come carta Soft.

---

## `Carta`

Rappresenta una singola carta del mazzo.

Contiene:

* seme;
* rango;
* valori possibili;
* stato coperta/scoperta;
* riferimento al mazzo.

---

## `Dealer`

Rappresenta il dealer e mantiene il riferimento alla propria mano.

Il comportamento decisionale del dealer non è contenuto direttamente nella classe, ma viene delegato alle strategie.

---

## `blackjackGameController`

È il **Controller applicativo** del gioco.

Espone le operazioni principali utilizzate dalla UI:

```java
avviaNuovaPartita(Long idGiocatore, Integer importoPuntata)

effettuaHit(Long idPartita)

effettuaStand(Long idPartita)
```

Il controller coordina il dominio e la persistenza senza contenere direttamente le regole di gioco.

---

# 🧠 Design Pattern

Il progetto utilizza principi e pattern di progettazione per mantenere il sistema modulare.

### Controller

Il:

```text
blackjackGameController
```

funge da punto di ingresso delle operazioni applicative provenienti dall'interfaccia.

---

### Strategy

Il comportamento del dealer è astratto attraverso:

```text
dealerStrategy
```

con una factory:

```text
dealerStrategyFactory
```

che permette di selezionare la strategia da utilizzare.

Esempi:

```text
Standard17
HitSoft17
```

Questo permette di aggiungere nuove varianti del comportamento del dealer senza modificare la logica principale di `Partita`.

---

### Data Access Object

La persistenza viene separata dalla logica di dominio attraverso DAO dedicati, ad esempio:

```text
partitaDAO
giocatoreDAO
```

---

# 🗄️ Persistenza

Il progetto utilizza:

* **Jakarta Persistence (JPA)**
* **Hibernate**
* **MySQL**

---

# 📋 Use Case

Il sistema è stato analizzato attraverso i seguenti Use Case principali.

| ID      | Use Case                                              | Attore    |
| ------- | ----------------------------------------------------- | --------- |
| **1.0** | Avviare una Partita con Configurazione Personalizzata | Giocatore |
| **2.0** | Effettuare una Mossa                                  | Giocatore |
| **3.0** | Determinare l'Esito della Partita                     | Sistema   |
| **4.0** | Turno del Dealer                                      | Sistema   |
| **5.0** | Consultare le Statistiche di Gioco                    | Giocatore |

### UC 1.0 — Avviare una partita

Il sistema crea una nuova partita, inizializza il mazzo, mescola le carte e distribuisce le carte iniziali.

### UC 2.0 — Effettuare una mossa

Il giocatore può effettuare una delle mosse previste dal Blackjack, tra cui Hit e Stand.

Le funzionalità Double e Split sono previste nell'analisi del sistema e potranno essere aggiunte successivamente.

### UC 3.0 — Determinare l'esito

Il sistema confronta i punteggi finali e determina:

```text
Vittoria
Sconfitta
Pareggio
```

tenendo conto delle condizioni speciali del Blackjack.

### UC 4.0 — Turno del Dealer

Il sistema scopre la carta coperta e utilizza la strategia configurata per decidere se effettuare Hit o Stand.

### UC 5.0 — Consultare le statistiche

È prevista una sezione dedicata alle statistiche delle partite, comprendente informazioni come:

* partite giocate;
* vittorie;
* sconfitte;
* pareggi;
* Win Rate;
* Blackjack;
* Bust;
* streak;
* puntata media;
* statistiche relative a Double e Split.

Questa funzionalità è prevista come estensione del progetto.

---

# 🛠️ Tecnologie utilizzate

| Tecnologia                  | Utilizzo                     |
| --------------------------- | ---------------------------- |
| **Java 17**                 | Linguaggio di programmazione |
| **Maven**                   | Build e gestione dipendenze  |
| **JavaFX 21**               | Interfaccia grafica          |
| **Jakarta Persistence 3.1** | ORM / persistenza            |
| **Hibernate 6.4**           | Implementazione JPA          |
| **MySQL**                   | Database                     |
| **Git / GitHub**            | Versionamento                |

---

# 🚀 Installazione

## Prerequisiti

Assicurarsi di avere installato:

* Java 17 o superiore;
* Maven;
* MySQL;
* Git.

Verificare Java:

```bash
java -version
```

Verificare Maven:

```bash
mvn -version
```

---

## Clonazione

Clonare il repository:

```bash
git clone <URL_REPOSITORY>
```

Entrare nella directory Maven:

```bash
cd CardGameEngineBlackjack/cardgame
```

---

## Compilazione

Eseguire:

```bash
mvn clean
```

oppure:

```bash
mvn clean compile
```

---

## Avvio dell'applicazione

Per avviare l'interfaccia JavaFX:

```bash
mvn javafx:run
```

L'applicazione utilizza:

```text
com.mycompany.CardGameEngineBlackjack.Main
```

come classe principale.

---

# 🧪 Test di integrazione

Il progetto contiene anche:

```text
MainTestIntegrazione.java
```

che permette di eseguire una simulazione della partita attraverso il controller applicativo senza utilizzare direttamente l'interfaccia JavaFX.

Il test verifica il flusso:

```text
Creazione partita
       ↓
Distribuzione carte
       ↓
Hit
       ↓
Stand
       ↓
Turno Dealer
       ↓
Determinazione esito
       ↓
Aggiornamento saldo
```

---

# 🔮 Sviluppi futuri

Il progetto è stato progettato con l'obiettivo di poter supportare diverse varianti del Blackjack.

Tra le possibili estensioni:

* Blackjack Europeo;
* Vegas Strip Blackjack;
* Single Deck Blackjack;
* configurazione del numero di mazzi;
* Dealer Stand/Hit Soft 17;
* regole personalizzabili per Double;
* regole personalizzabili per Split;
* Surrender;
* statistiche avanzate;
* profili di gioco;
* storico delle partite;
* miglioramento dell'interfaccia grafica.

L'utilizzo del pattern Strategy per il dealer e la separazione tra Domain, Application e TechnicalService facilitano l'introduzione di queste funzionalità.

---

# 📄 Documentazione

La documentazione del progetto comprende:

* Use Case;
* diagrammi UML;
* diagrammi di sequenza;
* diagrammi delle classi;
* documentazione relativa ai pattern GRASP e GoF;
* documentazione dell'architettura software.

Gli Use Case di riferimento sono:

```text
UC 1.0  Avviare una Partita con Configurazione Personalizzata
UC 2.0  Effettuare una Mossa
UC 3.0  Determinare l'Esito della Partita
UC 4.0  Turno del Dealer
UC 5.0  Consultare le Statistiche di Gioco
```

---

# 📜 Licenza

Progetto accademico sviluppato dal team **LudosGames**.

Versione **1.0**.
