package com.mycompany.CardGameEngineBlackjack;

import com.mycompany.CardGameEngineBlackjack.UI.MainController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        MainController controller = new MainController();

        Scene scene = new Scene(
                controller.getView().getRoot(),
                1000,
                700
        );

        stage.setTitle("Blackjack");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}