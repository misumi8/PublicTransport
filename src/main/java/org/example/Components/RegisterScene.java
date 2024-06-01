package org.example.Components;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.DAOs.UsersDAO;

public class RegisterScene {
    private String answer;
    private double totalWidth;
    private double totalHeight;

    public RegisterScene(double totalWidth, double totalHeight){
        this.totalHeight = totalHeight;
        this.totalWidth = totalWidth;
    }
    public String registerForm() {
        Stage passwordStage = new Stage();

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPrefWidth(this.totalWidth * 0.09);

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        cityField.setPrefWidth(this.totalWidth * 0.09);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(this.totalWidth * 0.09);

        Button registerButton = new Button("Register");

        VBox vBoxUser = new VBox(18, userLabel, passwordLabel, cityLabel);
        vBoxUser.setAlignment(Pos.CENTER);

        VBox vBoxPassword = new VBox(10, userField, passwordField, cityField);
        vBoxPassword.setAlignment(Pos.CENTER);

        HBox userPassword = new HBox(10);
        userPassword.getChildren().addAll(vBoxUser, vBoxPassword);
        userPassword.setAlignment(Pos.CENTER);

        VBox registerForm = new VBox(18);
        registerForm.getChildren().addAll(userPassword, registerButton);
        registerForm.setAlignment(Pos.CENTER);

        registerButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passwordField.getText();
            String city = cityField.getText();
            if(username.isEmpty()){
                Label errorMessage = new Label("The username field cannot be empty!");
                errorMessage.setStyle("-fx-text-fill: red;");
                registerForm.getChildren().clear();
                registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                userField.setStyle("-fx-background-color: #f78d8d;");
                userField.clear();
                userField.requestFocus();
            }
            else if(password.isEmpty()){
                System.out.println(123);
                Label errorMessage = new Label("The password field cannot be empty!");
                errorMessage.setStyle("-fx-text-fill: red;");
                registerForm.getChildren().clear();
                registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                passwordField.setStyle("-fx-background-color: #f78d8d;");
                passwordField.clear();
                passwordField.requestFocus();
            }
            else if(city.isEmpty()){
                Label errorMessage = new Label("The city field cannot be empty!");
                errorMessage.setStyle("-fx-text-fill: red;");
                registerForm.getChildren().clear();
                registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                cityField.setStyle("-fx-background-color: #f78d8d;");
                cityField.clear();
                cityField.requestFocus();
            }
            else {
                this.answer = UsersDAO.tryRegister(username, password, city);
                if (this.answer == null) {
                    passwordStage.close();
                }
                else if (this.answer.startsWith("ORA-20009")) {
                    Label errorMessage = new Label("Your username must be at least 8 characters!");
                    errorMessage.setStyle("-fx-text-fill: red;");
                    registerForm.getChildren().clear();
                    registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                    userField.setStyle("-fx-background-color: #f78d8d;");
                    userField.clear();
                    userField.requestFocus();
                } else if (this.answer.startsWith("ORA-20010")) {
                    Label errorMessage = new Label("Your password must contain at least one number!");
                    errorMessage.setStyle("-fx-text-fill: red;");
                    registerForm.getChildren().clear();
                    registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                    passwordField.setStyle("-fx-background-color: #f78d8d;");
                    passwordField.clear();
                    passwordField.requestFocus();
                } else if (this.answer.startsWith("ORA-20011")) {
                    Label errorMessage = new Label("Your password must contain at least one upper case letter!");
                    errorMessage.setStyle("-fx-text-fill: red;");
                    registerForm.getChildren().clear();
                    registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                    passwordField.setStyle("-fx-background-color: #f78d8d;");
                    passwordField.clear();
                    passwordField.requestFocus();
                } else if (this.answer.startsWith("ORA-20012")) {
                    Label errorMessage = new Label("Your password must be at least 8 characters!");
                    errorMessage.setStyle("-fx-text-fill: red;");
                    registerForm.getChildren().clear();
                    registerForm.getChildren().addAll(userPassword, registerButton, errorMessage);
                    passwordField.setStyle("-fx-background-color: #f78d8d;");
                    passwordField.clear();
                    passwordField.requestFocus();
                }
            }
        });

        registerForm.getStylesheets().add("styles/loginForm.css");
        Scene passwordScene = new Scene(registerForm);
        // On enter press passwordButton:
        passwordScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
            else if (event.getCode() == KeyCode.ESCAPE){
                passwordStage.close();
            }
        });
        passwordStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with other windows
        passwordStage.setScene(passwordScene);
        passwordStage.setHeight(totalHeight * 0.35);
        passwordStage.setWidth(totalWidth * 0.25);
        passwordStage.setResizable(false);
        passwordStage.setTitle("Register form");
        passwordStage.getIcons().add(new Image("/icons/icons8-underground-100.png"));
        passwordStage.initStyle(StageStyle.UNIFIED); // no

        passwordStage.centerOnScreen();
        passwordStage.showAndWait();
        passwordField.requestFocus();

        return this.answer;
    }
}
