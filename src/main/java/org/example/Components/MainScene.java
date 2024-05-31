package org.example.Components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.example.DAOs.*;
import org.example.Entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainScene {
    BorderPane root;
    double width;
    double height;


    public MainScene(double width, double height){
        this.root = new BorderPane();
        this.width = width;
        this.height = height;

        // Some tests:
        /*List<User> userss = UsersDAO.getUsers();
        for(User user : userss){
            System.out.println(user);
        }*/
        // end of the tests

        // Menu
        Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
        List<User> users = UsersDAO.getUsers();
        TreeItem<String> rootTreeNode = new TreeItem<>(); // try make it invisible
        /*rootTreeNode.addEventHandler(TreeItem.branchExpandedEvent(), event -> {
            event.consume(); // Отменяет событие развертывания
        });*/
        rootTreeNode.setExpanded(true);
        //rootTreeNode.getGraphic().getStyleClass().add("user"); doesn't work yet

        TreeView<String> menu = new TreeView<>(rootTreeNode);
        menu.setShowRoot(false);

        Map<Long, TreeItem<String>> usersMap = new HashMap<>();

        for(User user : users) {
            TreeItem<String> userRoot = new TreeItem<>(user.getUsername(), new ImageView(userIcon));
            usersMap.put(user.getId(), userRoot);
            userRoot.setExpanded(true);
            rootTreeNode.getChildren().add(userRoot);
        }

        menu.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = menu.getSelectionModel().getSelectedItem();
            if(selectedItem != null && selectedItem.getValue() != null) {
                if (!selectedItem.getValue().isEmpty() &&
                        usersMap.containsValue(selectedItem)) {
                    String username = selectedItem.getValue();
                    PasswordScene passwordScene = new PasswordScene(this.width, this.height, username);
                    long userId = passwordScene.loginForm();
                    if (userId > -1) {
                        usersMap.remove(userId);
                        List<Depot> userDepots = DepotsDAO.getDepotsOfUser(selectedItem.getValue()); // ???
                        Image depotIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-depot-21.png")));
                        for(Depot depot : userDepots) {
                            TreeItem<String> userDepot = new TreeItem<String>("Depot: " + depot.getId(), new ImageView(depotIcon));
                            userDepot.setExpanded(true);
                            selectedItem.getChildren().add(userDepot); // temp

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
                                    TreeItem<String> vehicleRoute = new TreeItem<String>("Route: " + route.getId(), new ImageView(routeIcon));
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
                        //selectedItem.setExpanded(true);
                        /*selectedItem.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
                            e.consume();
                        });*/
                    }
                }
                /*else {
                    selectedItem.setExpanded(true);
                }*/
            }
        });

        MultipleSelectionModel<TreeItem<String>> multipleSelectionModel = menu.getSelectionModel();
        multipleSelectionModel.selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> changed, TreeItem<String> oldValue, TreeItem<String> newValue) {
                System.out.println(" || " + newValue.getValue());
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
