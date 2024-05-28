package org.example.Components;

import com.sun.source.tree.Tree;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainFrame {
    BorderPane root;
    double width;
    double height;


    public MainFrame(double width, double height){
        this.root = new BorderPane();
        this.width = width;
        this.height = height;

        // Menu
        TreeItem<String> rootTreeNode = new TreeItem<>("username");
        TreeItem<String> depots = new TreeItem<>("Depots:");
        rootTreeNode.getChildren().add(depots);
        depots.getChildren().add(new TreeItem<String>("Depot1")); // temp
        depots.getChildren().add(new TreeItem<String>("Depot2")); // temp
        for(TreeItem<String> depot : depots.getChildren()){
            // Verifica daca exista masini in acest depot. Daca exista:
            TreeItem<String> vehicles = new TreeItem<>("Vehicles:");
            depot.getChildren().add(vehicles);
            vehicles.getChildren().add(new TreeItem<String>("Vehicle1")); // temp
            vehicles.getChildren().add(new TreeItem<String>("Vehicle2")); // temp
            vehicles.getChildren().add(new TreeItem<String>("Vehicle3")); // temp
            for(TreeItem<String> vehicle : vehicles.getChildren()){
                TreeItem<String> routes = new TreeItem<>("Routes:");
                vehicle.getChildren().add(routes);
            }
            TreeItem<String> routes = new TreeItem<>("Routes");
        }

        TreeView<String> menu = new TreeView<>(rootTreeNode);
        MultipleSelectionModel<TreeItem<String>> multipleSelectionModel = menu.getSelectionModel();
        multipleSelectionModel.selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> changed, TreeItem<String> oldValue, TreeItem<String> newValue) {
                System.out.println(newValue.getValue());
            }
        });
        menu.setMinWidth(this.width * 0.25);
        menu.setMinHeight(this.height);
        // Map
        Button button2 = new Button();
        button2.setMinWidth(this.width - menu.getMinWidth());
        button2.setMinHeight(this.height);
        button2.setText("Map - Settings");

        this.root.setLeft(menu);
        this.root.setRight(button2);
    }

    public BorderPane getRoot(){
        return this.root;
    }
}
