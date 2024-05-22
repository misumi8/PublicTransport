package org.example;

import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

public class Main extends Application{

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        Label lbl = new Label("Hello");
        Button btn = new Button("Click");

        VBox root = new VBox();
        root.getChildren().addAll(lbl, btn);

        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);

        stage.setTitle("Layout in JavaFX");

        stage.show();
    }
}