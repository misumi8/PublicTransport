package org.example.Components;

import com.sun.source.tree.Tree;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.DAOs.*;
import org.example.Entities.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainFrame {
    BorderPane root;
    double width;
    double height;


    public MainFrame(double width, double height){
        this.root = new BorderPane();
        this.width = width;
        this.height = height;

        // Some tests:
        List<User> userss = UsersDAO.getUsers();
        for(User user : userss){
            System.out.println(user);
        }
        // end of the tests

        // Menu
        Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
        List<User> users = UsersDAO.getUsers();
        TreeItem<String> rootTreeNode = new TreeItem<>("users"); // try make it invisible
        rootTreeNode.setExpanded(true);
        //rootTreeNode.getGraphic().getStyleClass().add("user"); doesn't work yet

        for(User user : users) {
            TreeItem<String> userRoot = new TreeItem<>(user.getUsername(), new ImageView(userIcon));
            //userRoot.setExpanded(false);
            rootTreeNode.getChildren().add(userRoot);

            List<Depot> userDepots = DepotsDAO.getDepotsOfUser(user.getId());
            Image depotIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-depot-21.png")));
            for(Depot depot : userDepots) {
                TreeItem<String> userDepot = new TreeItem<String>("" + depot.getId(), new ImageView(depotIcon));
                userDepot.setExpanded(true);
                userRoot.getChildren().add(userDepot); // temp

                List<Vehicle> depotVehicles = VehiclesDAO.getVehiclesOfDepot(depot.getId());
                for (Vehicle vehicle : depotVehicles) {
                    // Verifica daca exista masini in acest depot. Daca exista:

                    Image vehicleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-trolleybus-21.png")));
                    TreeItem<String> depotVehicle = new TreeItem<String>(vehicle.getPlate(), new ImageView(vehicleIcon));
                    depotVehicle.setExpanded(true);
                    userDepot.getChildren().add(depotVehicle); // temp

                    List<Route> vehicleRoutes = RoutesDAO.getRoutesOfVehicle(vehicle.getId());
                    for (Route route : vehicleRoutes) {
                        // Verifica daca exista rute masini in acest depot. Daca exista:

                        Image routeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-route-21.png")));
                        TreeItem<String> vehicleRoute = new TreeItem<String>("" + route.getId(), new ImageView(routeIcon));
                        vehicleRoute.setExpanded(true);
                        depotVehicle.getChildren().add(vehicleRoute); // temp

                        List<Station> routeStations = StationsDAO.getStationsOfRoute(route.getId());
                        for (Station station : routeStations) {
                            Image stationImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-train-station-21.png")));
                            TreeItem<String> routeStation = new TreeItem<String>(station.getPlacement(), new ImageView(stationImg));
                            routeStation.setExpanded(true);
                            vehicleRoute.getChildren().add(routeStation); // temp
                        }
                    }
                    TreeItem<String> routes = new TreeItem<>("Routes");
                }
            }
        }

        TreeView<String> menu = new TreeView<>(rootTreeNode);
        menu.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = menu.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedItem.setExpanded(true);
            }
        });

        MultipleSelectionModel<TreeItem<String>> multipleSelectionModel = menu.getSelectionModel();
        multipleSelectionModel.selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> changed, TreeItem<String> oldValue, TreeItem<String> newValue) {
                System.out.println(newValue.getValue());
            }
        });
        try {
            //URL treeViewCss = new URL("/styles/treeView.css");
            menu.getStylesheets().add("styles/treeView.css");
        } catch (NullPointerException e){
            System.out.println("NullPointerException: " + e);
        }
        menu.setPrefWidth(this.width * 0.2);
        menu.setPrefHeight(this.height);
        // Map
        Button button2 = new Button();
        button2.setPrefWidth(this.width - menu.getWidth());
        //System.out.println(this.width - menu.getWidth() + " || " + this.width * 0.25);
        button2.setPrefHeight(this.height);
        button2.setText("Map - Settings");

        this.root.setLeft(menu);
        this.root.setRight(button2);
    }

    public BorderPane getRoot(){
        return this.root;
    }
}
