package com.mycompany.CardGameEngineBlackjack.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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

        // =========================================================
        // ROOT
        // =========================================================

        root = new VBox(18);

        root.setAlignment(Pos.TOP_CENTER);

        root.setPadding(
                new Insets(28, 45, 28, 45)
        );

        root.setStyle(
                "-fx-background-color: #073b2a;"
        );

        // =========================================================
        // TITOLO
        // =========================================================

        titolo = new Label("BLACKJACK");

        titolo.setFont(
                Font.font("Arial", FontWeight.BOLD, 38)
        );

        titolo.setTextFill(Color.WHITE);

        titolo.setStyle(
                "-fx-letter-spacing: 5px;"
        );

        // =========================================================
        // DEALER
        // =========================================================

        dealerLabel = creaTitoloSezione("DEALER");

        dealerCarte = new HBox(16);

        dealerCarte.setAlignment(Pos.CENTER);

        dealerCarte.setMinHeight(118);

        dealerPunteggio = creaPunteggio("Punteggio: -");

        VBox sezioneDealer = creaSezioneGioco(
                dealerLabel,
                dealerCarte,
                dealerPunteggio
        );

        // =========================================================
        // SEPARATORE
        // =========================================================

        Label separatore = new Label("♦");

        separatore.setFont(
                Font.font("Arial", FontWeight.BOLD, 14)
        );

        separatore.setTextFill(
                Color.rgb(210, 170, 70)
        );

        // =========================================================
        // GIOCATORE
        // =========================================================

        giocatoreLabel = creaTitoloSezione("GIOCATORE");

        giocatoreCarte = new HBox(16);

        giocatoreCarte.setAlignment(Pos.CENTER);

        giocatoreCarte.setMinHeight(118);

        giocatorePunteggio = creaPunteggio("Punteggio: -");

        VBox sezioneGiocatore = creaSezioneGioco(
                giocatoreLabel,
                giocatoreCarte,
                giocatorePunteggio
        );

        // =========================================================
        // INFORMAZIONI FINANZIARIE
        // =========================================================

        saldoLabel = creaInfoLabel(
                "SALDO",
                "-"
        );

        puntataLabel = creaInfoLabel(
                "PUNTATA",
                "-"
        );

        HBox infoFinanziarie = new HBox(18);

        infoFinanziarie.setAlignment(Pos.CENTER);

        VBox saldoBox = creaBoxFinanziario(
                saldoLabel
        );

        VBox puntataBox = creaBoxFinanziario(
                puntataLabel
        );

        infoFinanziarie.getChildren().addAll(
                saldoBox,
                puntataBox
        );

        // =========================================================
        // MESSAGGIO
        // =========================================================

        messaggioLabel = new Label("");

        messaggioLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 20)
        );

        messaggioLabel.setTextFill(
                Color.rgb(239, 199, 72)
        );

        messaggioLabel.setAlignment(Pos.CENTER);

        messaggioLabel.setMinHeight(30);

        // =========================================================
        // PULSANTI
        // =========================================================

        nuovaPartitaButton = creaPulsante(
                "NUOVA PARTITA",
                "#b88a20",
                "#d4aa3a"
        );

        hitButton = creaPulsante(
                "HIT",
                "#176b45",
                "#218b5a"
        );

        standButton = creaPulsante(
                "STAND",
                "#8c3030",
                "#ae3d3d"
        );

        hitButton.setDisable(true);
        standButton.setDisable(true);

        HBox azioni = new HBox(14);

        azioni.setAlignment(Pos.CENTER);

        azioni.getChildren().addAll(
                nuovaPartitaButton,
                hitButton,
                standButton
        );

        // =========================================================
        // ASSEMBLAGGIO
        // =========================================================

        root.getChildren().addAll(
                titolo,
                sezioneDealer,
                separatore,
                sezioneGiocatore,
                infoFinanziarie,
                messaggioLabel,
                azioni
        );
    }

    // =============================================================
    // SEZIONE DI GIOCO
    // =============================================================

    private VBox creaSezioneGioco(
            Label titoloSezione,
            HBox carte,
            Label punteggio
    ) {

        VBox sezione = new VBox(9);

        sezione.setAlignment(Pos.CENTER);

        sezione.setPadding(
                new Insets(13, 25, 13, 25)
        );

        sezione.setMaxWidth(850);

        sezione.setStyle(
                "-fx-background-color: #0a4934;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #176b4a;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;"
        );

        VBox.setVgrow(carte, Priority.NEVER);

        sezione.getChildren().addAll(
                titoloSezione,
                carte,
                punteggio
        );

        return sezione;
    }

    // =============================================================
    // TITOLO DEALER / GIOCATORE
    // =============================================================

    private Label creaTitoloSezione(String testo) {

        Label label = new Label(testo);

        label.setFont(
                Font.font("Arial", FontWeight.BOLD, 16)
        );

        label.setTextFill(
                Color.rgb(235, 235, 235)
        );

        label.setStyle(
                "-fx-letter-spacing: 2px;"
        );

        return label;
    }

    // =============================================================
    // PUNTEGGIO
    // =============================================================

    private Label creaPunteggio(String testo) {

        Label label = new Label(testo);

        label.setFont(
                Font.font("Arial", FontWeight.BOLD, 14)
        );

        label.setTextFill(Color.WHITE);

        label.setPadding(
                new Insets(5, 14, 5, 14)
        );

        label.setStyle(
                "-fx-background-color: #116044;" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #1d7655;" +
                "-fx-border-radius: 20;"
        );

        return label;
    }

    // =============================================================
    // INFORMAZIONI FINANZIARIE
    // =============================================================

    private Label creaInfoLabel(
            String titoloInfo,
            String valore
    ) {

        Label label = new Label(
                titoloInfo + "  " + valore
        );

        label.setFont(
                Font.font("Arial", FontWeight.BOLD, 14)
        );

        label.setTextFill(Color.WHITE);

        return label;
    }

    private VBox creaBoxFinanziario(Label label) {

        VBox box = new VBox();

        box.setAlignment(Pos.CENTER);

        box.setMinWidth(185);

        box.setMinHeight(48);

        box.setPadding(
                new Insets(8, 22, 8, 22)
        );

        box.setStyle(
                "-fx-background-color: #0a3025;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #175540;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
        );

        box.getChildren().add(label);

        return box;
    }

    // =============================================================
    // PULSANTI
    // =============================================================

    private Button creaPulsante(
            String testo,
            String colore,
            String coloreHover
    ) {

        Button btn = new Button(testo);

        btn.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        btn.setTextFill(Color.WHITE);

        btn.setMinWidth(135);

        btn.setMinHeight(42);

        btn.setFocusTraversable(false);

        btn.setStyle(
                "-fx-background-color: " + colore + ";" +
                "-fx-background-radius: 9;" +
                "-fx-border-radius: 9;" +
                "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(event -> {

            if (!btn.isDisabled()) {

                btn.setStyle(
                        "-fx-background-color: " + coloreHover + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-radius: 9;" +
                        "-fx-cursor: hand;"
                );
            }
        });

        btn.setOnMouseExited(event -> {

            if (!btn.isDisabled()) {

                btn.setStyle(
                        "-fx-background-color: " + colore + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-radius: 9;" +
                        "-fx-cursor: hand;"
                );
            }
        });

        return btn;
    }

    // =============================================================
    // GETTERS
    // =============================================================

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
