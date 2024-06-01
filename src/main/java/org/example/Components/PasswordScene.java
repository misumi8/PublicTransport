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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        userField.setPrefWidth(this.totalWidth * 0.09);
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(this.totalWidth * 0.09);
        Button submitButton = new Button("Submit");

        VBox vBoxUser = new VBox(18, userLabel, passwordLabel);
        vBoxUser.setAlignment(Pos.CENTER);

        VBox vBoxPassword = new VBox(10, userField, passwordField);
        vBoxPassword.setAlignment(Pos.CENTER);

        HBox userPassword = new HBox(10);
        userPassword.getChildren().addAll(vBoxUser, vBoxPassword);
        userPassword.setAlignment(Pos.CENTER);

        VBox loginForm = new VBox(18);
        loginForm.getChildren().addAll(userPassword, submitButton);
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

        loginForm.getStylesheets().add("styles/loginForm.css");
        Scene passwordScene = new Scene(loginForm);
        // On enter press passwordButton:
        passwordScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submitButton.fire();
            }
            else if (event.getCode() == KeyCode.ESCAPE){
                passwordStage.close();
            }
        });
        passwordStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with other windows
        passwordStage.setScene(passwordScene);
        passwordStage.setHeight(totalHeight * 0.25);
        passwordStage.setWidth(totalWidth * 0.25);
        passwordStage.setResizable(false);
        passwordStage.setTitle("Login form");
        passwordStage.getIcons().add(new Image("/icons/icons8-underground-100.png"));
        passwordStage.initStyle(StageStyle.UNIFIED); // no

        passwordStage.showAndWait();
        passwordField.requestFocus();

        return this.userId;
    }

}
