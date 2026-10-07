package com.mycompany.CardGameEngineBlackjack.UI;

import com.mycompany.CardGameEngineBlackjack.Application.blackjackGameController;
import com.mycompany.CardGameEngineBlackjack.Domain.Carta;
import com.mycompany.CardGameEngineBlackjack.Domain.Mano;
import com.mycompany.CardGameEngineBlackjack.Domain.Partita;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

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

            partitaCorrente =
                    gameController.avviaNuovaPartita(
                            idGiocatore,
                            importoPuntata
                    );

            aggiornaInterfaccia();

            view.getMessaggioLabel().setText("");

            if (partitaCorrente.getStato()
                    == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.IN_CORSO) {

                view.getHitButton().setDisable(false);
                view.getStandButton().setDisable(false);
            } else {

                view.getHitButton().setDisable(true);
                view.getStandButton().setDisable(true);
            }

        } catch (Exception e) {

            view.getMessaggioLabel().setText(
                    "Errore: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void effettuaHit() {

        if (partitaCorrente == null) {
            return;
        }

        try {

            partitaCorrente =
                    gameController.effettuaHit(
                            partitaCorrente.getId()
                    );

            aggiornaInterfaccia();

            if (partitaCorrente.getStato()
                    == com.mycompany.CardGameEngineBlackjack.Domain.Enum.statoPartita.TERMINATA) {

                terminaPartita();
            }

        } catch (Exception e) {

            view.getMessaggioLabel().setText(
                    "Errore: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void effettuaStand() {

        if (partitaCorrente == null) {
            return;
        }

        try {

            partitaCorrente =
                    gameController.effettuaStand(
                            partitaCorrente.getId()
                    );

            aggiornaInterfaccia();

            terminaPartita();

        } catch (Exception e) {

            view.getMessaggioLabel().setText(
                    "Errore: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void aggiornaInterfaccia() {

        if (partitaCorrente == null) {
            return;
        }

        aggiornaManoGiocatore();
        aggiornaManoDealer();
        aggiornaInformazioni();
    }

    private void aggiornaManoGiocatore() {

        Mano manoGiocatore =
                partitaCorrente.getMani().get(0);

        view.getGiocatoreCarte().getChildren().clear();

        for (Carta carta : manoGiocatore.getCarte()) {

            view.getGiocatoreCarte()
                    .getChildren()
                    .add(creaCartaLabel(carta));
        }

        view.getGiocatorePunteggio().setText(
                "Punteggio: " + manoGiocatore.getPunteggio()
        );
    }

    private void aggiornaManoDealer() {

        Mano manoDealer =
                partitaCorrente.getDealer().getMano();

        view.getDealerCarte().getChildren().clear();

        int punteggioVisibile = 0;

        for (Carta carta : manoDealer.getCarte()) {

            view.getDealerCarte()
                    .getChildren()
                    .add(creaCartaLabel(carta));

            if (!carta.getCoperta()) {

                if (carta.getRango()
                        == com.mycompany.CardGameEngineBlackjack.Domain.Enum.Rango.ASSO) {

                    punteggioVisibile += 11;

                } else {

                    punteggioVisibile +=
                            carta.getValoriPossibili().get(0);
                }
            }
        }

        boolean cartaCoperta = false;

        for (Carta carta : manoDealer.getCarte()) {

            if (carta.getCoperta()) {
                cartaCoperta = true;
                break;
            }
        }

        if (cartaCoperta) {

            view.getDealerPunteggio().setText(
                    "Punteggio: " + punteggioVisibile + " + ?"
            );

        } else {

            view.getDealerPunteggio().setText(
                    "Punteggio: " + manoDealer.getPunteggio()
            );
        }
    }

    private Label creaCartaLabel(Carta carta) {

        Label label;

        if (carta.getCoperta()) {

            label = new Label("🂠");

        } else {

            String simboloSeme =
                    getSimboloSeme(carta);

            String nomeRango =
                    carta.getRango().toString();

            label = new Label(
                    nomeRango + "\n" + simboloSeme
            );
        }

        label.setMinSize(70, 100);
        label.setPrefSize(70, 100);
        label.setAlignment(Pos.CENTER);

        label.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 1;" +
                "-fx-font-size: 18px;"
        );

        label.setFont(Font.font("Arial", 18));

        return label;
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
                "Saldo: "
                + partitaCorrente
                    .getGiocatore()
                    .getBilancioFiches()
                + " fiches"
        );

        Mano manoGiocatore =
                partitaCorrente.getMani().get(0);

        view.getPuntataLabel().setText(
                "Puntata: "
                + manoGiocatore
                    .getPuntata()
                    .getValoreTotale()
                + " fiches"
        );
    }

    private void terminaPartita() {

        view.getHitButton().setDisable(true);
        view.getStandButton().setDisable(true);

        switch (partitaCorrente.getEsito()) {

            case VITTORIA_GIOCATORE:
                view.getMessaggioLabel().setText(
                        "HAI VINTO!"
                );
                break;

            case VITTORIA_DEALER:
                view.getMessaggioLabel().setText(
                        "HA VINTO IL DEALER!"
                );
                break;

            case PAREGGIO:
                view.getMessaggioLabel().setText(
                        "PAREGGIO!"
                );
                break;

            default:
                view.getMessaggioLabel().setText(
                        partitaCorrente.getEsito().toString()
                );
        }
    }

    public MainView getView() {
        return view;
    }
}