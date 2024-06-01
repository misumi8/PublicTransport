package org.example;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.example.Components.MainScene;

public class Main extends Application{

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(new MainScene(screenBounds.getWidth(), screenBounds.getHeight()).getRoot());
        stage.setScene(scene);
        stage.setTitle("Public Transport v0.6");
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.getIcons().add(new Image("/icons/icons8-underground-100.png"));
        stage.setMaximized(true);

        stage.show();
    }
}