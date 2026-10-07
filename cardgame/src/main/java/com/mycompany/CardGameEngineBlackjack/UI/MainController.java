package com.mycompany.CardGameEngineBlackjack.UI;

import com.mycompany.CardGameEngineBlackjack.Application.blackjackGameController;
import com.mycompany.CardGameEngineBlackjack.Domain.Carta;
import com.mycompany.CardGameEngineBlackjack.Domain.Mano;
import com.mycompany.CardGameEngineBlackjack.Domain.Partita;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainController {

    private final blackjackGameController gameController;
    private final MainView view;
    private Partita partitaCorrente;

    // Per ora utilizziamo il giocatore 1 e una puntata fissa di 50.
    private final Long idGiocatore = 1L;
    private final int importoPuntata = 50;

    public MainController() {
        this.gameController = new blackjackGameController();
        this.view = new MainView();
        configuraEventi();
    }

    private void configuraEventi() {
        view.getNuovaPartitaButton().setOnAction(event -> avviaPartita());
        view.getHitButton().setOnAction(event -> effettuaHit());
        view.getStandButton().setOnAction(event -> effettuaStand());
    }

    private void avviaPartita() {
        try {
            partitaCorrente = gameController.avviaNuovaPartita(idGiocatore, importoPuntata);
            aggiornaInterfaccia();
            view.getMessaggioLabel().setText("");

            if (partitaCorrente.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {
                view.getHitButton().setDisable(false);
                view.getStandButton().setDisable(false);
            } else {
                view.getHitButton().setDisable(true);
                view.getStandButton().setDisable(true);
            }
        } catch (Exception e) {
            view.getMessaggioLabel().setText("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void effettuaHit() {
        if (partitaCorrente == null) return;

        try {
            partitaCorrente = gameController.effettuaHit(partitaCorrente.getId());
            aggiornaInterfaccia();

            if (partitaCorrente.getStato() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.TERMINATA) {
                terminaPartita();
            }
        } catch (Exception e) {
            view.getMessaggioLabel().setText("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void effettuaStand() {
        if (partitaCorrente == null) return;

        try {
            partitaCorrente = gameController.effettuaStand(partitaCorrente.getId());
            aggiornaInterfaccia();
            terminaPartita();
        } catch (Exception e) {
            view.getMessaggioLabel().setText("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void aggiornaInterfaccia() {
        if (partitaCorrente == null) return;
        aggiornaManoGiocatore();
        aggiornaManoDealer();
        aggiornaInformazioni();
    }

    // --- METODO DI SUPPORTO PER L'ESTRAZIONE SICURA ---
    private Mano estraiManoGiocatore() {
        for (Mano m : partitaCorrente.getMani()) {
            if (m.getGiocatore() != null) {
                return m;
            }
        }
        throw new IllegalStateException("Mano del giocatore non trovata nella partita.");
    }

    private void aggiornaManoGiocatore() {
        Mano manoGiocatore = estraiManoGiocatore();
        view.getGiocatoreCarte().getChildren().clear();

        for (Carta carta : manoGiocatore.getCarte()) {
            view.getGiocatoreCarte().getChildren().add(creaCartaLabel(carta));
        }

        view.getGiocatorePunteggio().setText("Punteggio: " + manoGiocatore.getPunteggio());
    }

    private void aggiornaManoDealer() {
        Mano manoDealer = partitaCorrente.getDealer().getMano();
        view.getDealerCarte().getChildren().clear();

        int punteggioVisibile = 0;
        boolean cartaCoperta = false;

        for (Carta carta : manoDealer.getCarte()) {
            view.getDealerCarte().getChildren().add(creaCartaLabel(carta));

            if (!carta.getCoperta()) {
                if (carta.getRango() == com.mycompany.CardGameEngineBlackjack.Domain.Enum.Rango.ASSO) {
                    punteggioVisibile += 11;
                } else {
                    punteggioVisibile += carta.getValoriPossibili().get(0);
                }
            } else {
                cartaCoperta = true;
            }
        }

        if (cartaCoperta) {
            view.getDealerPunteggio().setText("Punteggio: " + punteggioVisibile + " + ?");
        } else {
            view.getDealerPunteggio().setText("Punteggio: " + manoDealer.getPunteggio());
        }
    }

    private VBox creaCartaLabel(Carta carta) {

        VBox cartaBox = new VBox();

        cartaBox.setPrefSize(78, 108);
        cartaBox.setMinSize(78, 108);
        cartaBox.setMaxSize(78, 108);

        cartaBox.setAlignment(Pos.CENTER);

        // =========================================================
        // CARTA COPERTA
        // =========================================================

        if (carta.getCoperta()) {

            Label dorso = new Label("♠");

            dorso.setFont(Font.font("Arial", 32));
            dorso.setTextFill(javafx.scene.paint.Color.WHITE);

            dorso.setAlignment(Pos.CENTER);

            cartaBox.getChildren().add(dorso);

            cartaBox.setStyle(
                    "-fx-background-color: #173b8f;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #d4af37;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 8, 0.2, 0, 3);"
            );

            return cartaBox;
        }

        // =========================================================
        // CARTA SCOPERTA
        // =========================================================

        String simboloSeme = getSimboloSeme(carta);
        String nomeRango = getValoreRango(carta);

        boolean rossa =
                simboloSeme.equals("♥") ||
                simboloSeme.equals("♦");

        String colore = rossa ? "#c62828" : "#151515";

        // Valore della carta
        Label valore = new Label(nomeRango);

        valore.setFont(
                Font.font("Arial", FontWeight.BOLD, 18)
        );

        valore.setTextFill(
                javafx.scene.paint.Color.web(colore)
        );

        // Seme
        Label seme = new Label(simboloSeme);

        seme.setFont(
                Font.font("Arial", FontWeight.BOLD, 36)
        );

        seme.setTextFill(
                javafx.scene.paint.Color.web(colore)
        );

        // =========================================================
        // DISPOSIZIONE
        // =========================================================

        cartaBox.setSpacing(2);

        cartaBox.getChildren().addAll(
                valore,
                seme
        );

        cartaBox.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #d6d6d6;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 8, 0.2, 0, 3);"
        );

        return cartaBox;
    }

    private String getSimboloSeme(Carta carta) {
        switch (carta.getSeme().toString().toUpperCase()) {
            case "CUORI":
            case "CUORE":
                return "♥";
            case "QUADRI":
            case "QUADRO":
                return "♦";
            case "FIORI":
            case "FIORE":
                return "♣";
            case "PICCHE":
                return "♠";
            default:
                return carta.getSeme().toString();
        }
    }

    private void aggiornaInformazioni() {
        view.getSaldoLabel().setText(
                "Saldo: " + partitaCorrente.getGiocatore().getBilancioFiches() + " fiches"
        );

        Mano manoGiocatore = estraiManoGiocatore();
        view.getPuntataLabel().setText(
                "Puntata: " + manoGiocatore.getPuntata().getValoreTotale() + " fiches"
        );
    }

    private void terminaPartita() {
        view.getHitButton().setDisable(true);
        view.getStandButton().setDisable(true);

        switch (partitaCorrente.getEsito()) {
            case VITTORIA_GIOCATORE:
                view.getMessaggioLabel().setText("HAI VINTO!");
                break;
            case VITTORIA_DEALER:
                view.getMessaggioLabel().setText("HA VINTO IL DEALER!");
                break;
            case PAREGGIO:
                view.getMessaggioLabel().setText("PAREGGIO!");
                break;
            default:
                view.getMessaggioLabel().setText(partitaCorrente.getEsito().toString());
        }
    }

    private String getValoreRango(Carta carta) {

        switch (carta.getRango().toString().toUpperCase()) {

            case "ASSO":
                return "A";

            case "DUE":
                return "2";

            case "TRE":
                return "3";

            case "QUATTRO":
                return "4";

            case "CINQUE":
                return "5";

            case "SEI":
                return "6";

            case "SETTE":
                return "7";

            case "OTTO":
                return "8";

            case "NOVE":
                return "9";

            case "DIECI":
                return "10";

            case "JACK":
                return "J";

            case "REGINA":
                return "Q";

            case "RE":
                return "K";

            default:
                return carta.getRango().toString();
        }
    }

    public MainView getView() {
        return view;
    }
}