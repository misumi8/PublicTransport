package org.example.Components;

import javafx.css.StyleClass;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.DAOs.UsersDAO;
import org.example.Entities.User;

import java.text.Format;

public class PasswordScene {
    private long userId = -1L;
    private double totalWidth;
    private double totalHeight;
    private String username;

    public PasswordScene(double totalWidth, double totalHeight, String username){
        this.totalHeight = totalHeight;
        this.totalWidth = totalWidth;
        this.username = username;
    }
    public long loginForm() {
        Stage passwordStage = new Stage();

        Label userLabel = new Label("Username:");
        TextField userField = new TextField(this.username);
        userField.setFocusTraversable(false);
        userField.setEditable(false);
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button submitButton = new Button("Submit");

        HBox hBoxUser = new HBox(10, userLabel, userField);
        //hBoxUser.setPadding(new Insets(5, 0, 5, 0));
        hBoxUser.setAlignment(Pos.CENTER);

        HBox hBoxPassword = new HBox(10, passwordLabel, passwordField);
        //hBoxPassword.setPadding(new Insets(5, 0, 5, 0));
        hBoxPassword.setAlignment(Pos.CENTER);

        VBox loginForm = new VBox(10);
        loginForm.getChildren().addAll(hBoxUser, hBoxPassword, submitButton);
        loginForm.setAlignment(Pos.CENTER);

        submitButton.setOnAction(e -> {
            this.userId = UsersDAO.tryLogin(this.username, passwordField.getText());
            if (this.userId > -1L) {
                passwordStage.close();
            }
            else {
                passwordField.setStyle("-fx-background-color: #f78d8d;");
                passwordField.clear();
                passwordField.requestFocus();
            }
        });

        loginForm.setStyle("-fx-background-color: #DDDDDD;");
        Scene passwordScene = new Scene(loginForm);
        passwordStage.initModality(Modality.APPLICATION_MODAL); // Blocks other windows interaction
        passwordStage.setScene(passwordScene);
        passwordStage.setHeight(totalHeight * 0.25);
        passwordStage.setWidth(totalWidth * 0.25);
        passwordStage.setResizable(false);
        passwordStage.setTitle("Login form");
        passwordStage.showAndWait();
        passwordField.requestFocus();

        return this.userId;
    }

}
