package com.mycompany.CardGameEngineBlackjack.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30)); // Aggiunto margine per distanziare gli elementi dai bordi
        root.setStyle("-fx-background-color: #176B3A;"); // Verde tipico dei tavoli da casinò

        titolo = new Label("BLACKJACK ENGINE");
        titolo.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titolo.setTextFill(Color.WHITE);

        // --- SEZIONE DEALER ---
        dealerLabel = new Label("DEALER");
        dealerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        dealerLabel.setTextFill(Color.WHITE);

        dealerCarte = new HBox(15);
        dealerCarte.setAlignment(Pos.CENTER);
        dealerCarte.setMinHeight(100); // Mantiene lo spazio anche quando vuoto

        dealerPunteggio = new Label("Punteggio: -");
        dealerPunteggio.setFont(Font.font("Arial", 16));
        dealerPunteggio.setTextFill(Color.WHITE);

        // --- SEZIONE GIOCATORE ---
        giocatoreLabel = new Label("GIOCATORE");
        giocatoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        giocatoreLabel.setTextFill(Color.WHITE);

        giocatoreCarte = new HBox(15);
        giocatoreCarte.setAlignment(Pos.CENTER);
        giocatoreCarte.setMinHeight(100);

        giocatorePunteggio = new Label("Punteggio: -");
        giocatorePunteggio.setFont(Font.font("Arial", 16));
        giocatorePunteggio.setTextFill(Color.WHITE);

        // --- SEZIONE FINANZIARIA ---
        saldoLabel = new Label("Saldo: -");
        saldoLabel.setFont(Font.font("Arial", 16));
        saldoLabel.setTextFill(Color.LIGHTYELLOW);

        puntataLabel = new Label("Puntata: -");
        puntataLabel.setFont(Font.font("Arial", 16));
        puntataLabel.setTextFill(Color.LIGHTYELLOW);

        // Raggruppate sulla stessa riga
        HBox infoFinanziarie = new HBox(40);
        infoFinanziarie.setAlignment(Pos.CENTER);
        infoFinanziarie.getChildren().addAll(saldoLabel, puntataLabel);

        // --- SEZIONE AZIONI E MESSAGGI ---
        messaggioLabel = new Label("");
        messaggioLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        messaggioLabel.setTextFill(Color.GOLD);

        nuovaPartitaButton = creaPulsanteStilizzato("NUOVA PARTITA");
        hitButton = creaPulsanteStilizzato("HIT");
        standButton = creaPulsanteStilizzato("STAND");

        hitButton.setDisable(true);
        standButton.setDisable(true);

        HBox azioni = new HBox(20);
        azioni.setAlignment(Pos.CENTER);
        azioni.getChildren().addAll(nuovaPartitaButton, hitButton, standButton);

        // --- ASSEMBLAGGIO ROOT ---
        root.getChildren().addAll(
                titolo,
                dealerLabel,
                dealerCarte,
                dealerPunteggio,
                giocatoreLabel,
                giocatoreCarte,
                giocatorePunteggio,
                infoFinanziarie,
                azioni,
                messaggioLabel
        );
    }

    // Metodo privato per standardizzare il design dei bottoni
    private Button creaPulsanteStilizzato(String testo) {
        Button btn = new Button(testo);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle(
                "-fx-background-color: #333333;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-cursor: hand;"
        );
        
        // Effetto Hover di base
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        
        return btn;
    }

    public VBox getRoot() { return root; }
    public Label getDealerPunteggio() { return dealerPunteggio; }
    public HBox getDealerCarte() { return dealerCarte; }
    public Label getGiocatorePunteggio() { return giocatorePunteggio; }
    public HBox getGiocatoreCarte() { return giocatoreCarte; }
    public Label getSaldoLabel() { return saldoLabel; }
    public Label getPuntataLabel() { return puntataLabel; }
    public Label getMessaggioLabel() { return messaggioLabel; }
    public Button getNuovaPartitaButton() { return nuovaPartitaButton; }
    public Button getHitButton() { return hitButton; }
    public Button getStandButton() { return standButton; }
}