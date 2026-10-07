package com.mycompany.CardGameEngineBlackjack.UI;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MainView {

    private final VBox root;

    private final Label titolo;

    private final Label dealerLabel;
    private final HBox dealerCarte;
    private final Label dealerPunteggio;

    private final Label giocatoreLabel;
    private final HBox giocatoreCarte;
    private final Label giocatorePunteggio;

    private final Label saldoLabel;
    private final Label puntataLabel;
    private final Label messaggioLabel;

    private final Button nuovaPartitaButton;
    private final Button hitButton;
    private final Button standButton;

    public MainView() {

        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #176B3A;");

        titolo = new Label("BLACKJACK");
        titolo.setFont(Font.font("Arial", 32));
        titolo.setTextFill(Color.WHITE);

        dealerLabel = new Label("DEALER");
        dealerLabel.setFont(Font.font("Arial", 20));
        dealerLabel.setTextFill(Color.WHITE);

        dealerCarte = new HBox(10);
        dealerCarte.setAlignment(Pos.CENTER);

        dealerPunteggio = new Label("Punteggio: -");
        dealerPunteggio.setTextFill(Color.WHITE);

        giocatoreLabel = new Label("GIOCATORE");
        giocatoreLabel.setFont(Font.font("Arial", 20));
        giocatoreLabel.setTextFill(Color.WHITE);

        giocatoreCarte = new HBox(10);
        giocatoreCarte.setAlignment(Pos.CENTER);

        giocatorePunteggio = new Label("Punteggio: -");
        giocatorePunteggio.setTextFill(Color.WHITE);

        saldoLabel = new Label("Saldo: -");
        saldoLabel.setTextFill(Color.WHITE);

        puntataLabel = new Label("Puntata: -");
        puntataLabel.setTextFill(Color.WHITE);

        messaggioLabel = new Label("");
        messaggioLabel.setFont(Font.font("Arial", 18));
        messaggioLabel.setTextFill(Color.WHITE);

        nuovaPartitaButton = new Button("NUOVA PARTITA");
        hitButton = new Button("HIT");
        standButton = new Button("STAND");

        hitButton.setDisable(true);
        standButton.setDisable(true);

        HBox azioni = new HBox(15);
        azioni.setAlignment(Pos.CENTER);
        azioni.getChildren().addAll(
                nuovaPartitaButton,
                hitButton,
                standButton
        );

        root.getChildren().addAll(
                titolo,

                dealerLabel,
                dealerCarte,
                dealerPunteggio,

                giocatoreLabel,
                giocatoreCarte,
                giocatorePunteggio,

                saldoLabel,
                puntataLabel,

                azioni,
                messaggioLabel
        );
    }

    public VBox getRoot() {
        return root;
    }

    public Label getDealerPunteggio() {
        return dealerPunteggio;
    }

    public HBox getDealerCarte() {
        return dealerCarte;
    }

    public Label getGiocatorePunteggio() {
        return giocatorePunteggio;
    }

    public HBox getGiocatoreCarte() {
        return giocatoreCarte;
    }

    public Label getSaldoLabel() {
        return saldoLabel;
    }

    public Label getPuntataLabel() {
        return puntataLabel;
    }

    public Label getMessaggioLabel() {
        return messaggioLabel;
    }

    public Button getNuovaPartitaButton() {
        return nuovaPartitaButton;
    }

    public Button getHitButton() {
        return hitButton;
    }

    public Button getStandButton() {
        return standButton;
    }
}