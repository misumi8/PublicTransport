package org.example.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.DAOs.UsersDAO;

public class ConfirmationScene {
    private double totalWidth;
    private double totalHeight;
    private String username;

    public ConfirmationScene(double totalWidth, double totalHeight, String username){
        this.totalHeight = totalHeight;
        this.totalWidth = totalWidth;
        this.username = username;
    }
    public void confirmationForm() {
        Stage confirmationStage = new Stage();

        Label confirmationLabel = new Label("Are you sure you want to delete user \"" + this.username + "\"?");
        Button yes = new Button("Yes");
        yes.setPrefWidth(this.totalWidth * 0.03);
        FlowPane.setMargin(yes, new Insets(0,10,10,0));
        yes.setOnMouseClicked(e -> {
            UsersDAO.deleteUser(this.username);
            confirmationStage.close();
        });
        Button no = new Button("No");
        no.setPrefWidth(this.totalWidth * 0.03);
        FlowPane.setMargin(no, new Insets(0,25,10,0));
        no.setOnMouseClicked(e -> {
            confirmationStage.close();
        });
        FlowPane buttons = new FlowPane();
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.setHgap(10);
        buttons.setVgap(5);
        buttons.getChildren().addAll(yes, no);

        BorderPane root = new BorderPane();
        BorderPane.setMargin(confirmationLabel, new Insets(9,0,0,0));
        root.setCenter(confirmationLabel);
        root.setBottom(buttons);

        Scene confirmationScene = new Scene(root);
        confirmationScene.getStylesheets().add("/styles/confirmationForm.css");
        confirmationScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE){
                confirmationStage.close();
            }
        });
        confirmationStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with other windows
        confirmationStage.setScene(confirmationScene);
        confirmationStage.setHeight(totalHeight * 0.22);
        confirmationStage.setWidth(totalWidth * 0.26);
        confirmationStage.setResizable(false);
        confirmationStage.setTitle("Delete confirmation");
        confirmationStage.getIcons().add(new Image("/icons/icons8-underground-100.png"));
        confirmationStage.initStyle(StageStyle.UNIFIED); // no

        confirmationStage.centerOnScreen();
        confirmationStage.showAndWait();
        confirmationStage.requestFocus();
    }
}
