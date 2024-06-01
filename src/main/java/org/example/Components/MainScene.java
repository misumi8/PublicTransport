package org.example.Components;

import com.sun.source.tree.Tree;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.example.DAOs.*;
import org.example.Entities.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class MainScene {
    private BorderPane root;
    private double width;
    private double height;
    private TreeItem<String> rootTreeNode;

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

        TreeView<String> menuList = new TreeView<>();
        menuList.setShowRoot(false);
        menuList.setPrefWidth(this.width * 0.2);

        HBox menuButtons = new HBox(0);
        menuButtons.getStylesheets().add("styles/menuButtons.css");
        Button update = new Button();
        update.setOnAction(e->update.setStyle("-fx-background-color: #D3D3D3"));
        Button registerUser = new Button();
        registerUser.setOnAction(e-> {
            registerUser.setStyle("-fx-background-color: #D3D3D3;");
            RegisterScene registerScene = new RegisterScene(this.width, this.height);
            registerScene.registerForm();
            registerUser.setStyle("-fx-background-color: #DDDDDD;");
        });

        registerUser.setGraphic(new ImageView(new Image("/icons/icons8-add-21.png")));
        update.setGraphic(new ImageView(new Image("/icons/icons8-update-21.png")));
        menuButtons.getChildren().addAll(registerUser, update);
        menuButtons.setPrefWidth(this.width * 0.2);
        menuButtons.setPrefHeight(this.height - menuList.getHeight());
        menuButtons.setMaxHeight(this.height - menuList.getHeight());
        menuButtons.getStyleClass().add("menuButtons");

        // Menu
        BorderPane menu = new BorderPane();
        menu.setPrefHeight(this.height);
        menu.setPrefWidth(this.width * 0.2);
        menu.setMaxHeight(this.height);
        menu.setMaxWidth(this.width * 0.2);
        menu.setTop(menuList);
        menu.setBottom(menuButtons);
        menuList.setPrefHeight(this.height * 0.935);
        menuButtons.setPrefHeight(this.height * 0.065);
        menuList.setMaxHeight(this.height * 0.935);
        menuButtons.setMaxHeight(this.height * 0.065);
        this.root.setLeft(menu);
        // Menu list
        List<User> users = UsersDAO.getUsers();
        TreeItem<String> rootTreeNode = new TreeItem<>();
        this.rootTreeNode = rootTreeNode;
        menuList.setRoot(rootTreeNode);
        rootTreeNode.setExpanded(true);
        Set<String> loggedUsers = new HashSet<>();
        Map<Long, String> usersMap = new HashMap<>();
        update.setOnMouseClicked(e -> {
            //System.out.println(123);
            rootTreeNode.getChildren().clear();
            List<User> usersList = UsersDAO.getUsers();
            for(User user : usersList) {
                Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
                if(loggedUsers.contains(user.getUsername())) {
                    TreeItem<String> userRoot = getUserInfoTreeItem(user.getUsername());
                    userRoot.setExpanded(true);
                    menuList.getRoot().getChildren().add(userRoot);
                } else {
                    TreeItem<String> userRoot = new TreeItem<>(user.getUsername(), new ImageView(userIcon));
                    usersMap.put(user.getId(), userRoot.getValue());
                    userRoot.setExpanded(true);
                    menuList.getRoot().getChildren().add(userRoot);
                }
            }
        });

        for(User user : users) {
            Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
            TreeItem<String> userRoot = new TreeItem<>(user.getUsername(), new ImageView(userIcon));
            usersMap.put(user.getId(), userRoot.getValue());
            userRoot.setExpanded(true);
            rootTreeNode.getChildren().add(userRoot);
        }

        menuList.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if(selectedItem != null && selectedItem.getValue() != null) {
                // user item:
                if (!selectedItem.getValue().isEmpty() &&
                        usersMap.containsValue(selectedItem.getValue())) {
                    String username = selectedItem.getValue();
                    PasswordScene passwordScene = new PasswordScene(this.width, this.height, username);
                    long userId = passwordScene.loginForm();
                    if (userId > -1) {
                        usersMap.remove(userId);
                        List<Depot> userDepots = DepotsDAO.getDepotsOfUser(username);
                        Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
                        Image depotIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-depot-21.png")));
                        for(Depot depot : userDepots) {
                            TreeItem<String> userDepot = new TreeItem<String>("Depot: " + depot.getId(), new ImageView(depotIcon));
                            userDepot.setExpanded(true);
                            selectedItem.getChildren().add(userDepot); // temp

                            List<Vehicle> depotVehicles = VehiclesDAO.getVehiclesOfDepot(depot.getId());
                            for (Vehicle vehicle : depotVehicles) {
                                Image vehicleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-trolleybus-21.png")));
                                TreeItem<String> depotVehicle = new TreeItem<String>(vehicle.getPlate(), new ImageView(vehicleIcon));
                                depotVehicle.setExpanded(true);
                                userDepot.getChildren().add(depotVehicle); // temp

                                List<Route> vehicleRoutes = RoutesDAO.getRoutesOfVehicle(vehicle.getId());
                                for (Route route : vehicleRoutes) {
                                    Image routeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-route-21.png")));
                                    TreeItem<String> vehicleRoute = new TreeItem<String>("Route: " + route.getId(), new ImageView(routeIcon));
                                    vehicleRoute.setExpanded(true);
                                    depotVehicle.getChildren().add(vehicleRoute); // temp

                                    List<Station> routeStations = StationsDAO.getStationsOfRoute(route.getId());
                                    for (Station station : routeStations) {
                                        Image stationImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-train-station-19.png")));
                                        TreeItem<String> routeStation = new TreeItem<String>(station.getPlacement(), new ImageView(stationImg));
                                        routeStation.getGraphic().getStyleClass().add("stationRouteImg");
                                        routeStation.setExpanded(true);
                                        vehicleRoute.getChildren().add(routeStation); // temp
                                    }
                                }
                                TreeItem<String> routes = new TreeItem<>("Routes");
                            }
                        }
                        loggedUsers.add(selectedItem.getValue());
                    }
                }
                if(selectedItem.getValue().startsWith("Depot: ")){
                    Long depotId = Long.parseLong(selectedItem.getValue().substring(7));
                    System.out.println("|" + depotId + "|");
                    ObservableList<Schedule> schedules = FXCollections.observableList(Objects.requireNonNull(ScheduleDAO.getAllData(depotId)));
                    TableView<Schedule> table = new TableView<>(schedules);
                    table.setPrefWidth(this.width - menu.getWidth());
                    table.setPrefHeight(this.height);
                    table.setMaxWidth(this.width - menu.getWidth());
                    table.setMaxHeight(this.height);
                    double tableWidth = this.width - menu.getWidth();
                    TableColumn<Schedule, Long> depotIdColumn = new TableColumn<>("Depot ID");
                    depotIdColumn.setCellValueFactory(new PropertyValueFactory<Schedule, Long>("depotId"));
                    depotIdColumn.setPrefWidth(tableWidth / 4);
                    //depotIdColumn.prefWidthProperty().bind(table.widthProperty().divide(4));
                    table.getColumns().add(depotIdColumn);

                    TableColumn<Schedule, Long> vehicleIdColumn = new TableColumn<>("Vehicle ID");
                    vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<Schedule, Long>("vehicleId"));
                    vehicleIdColumn.setPrefWidth(tableWidth / 4);
                    //vehicleIdColumn.prefWidthProperty().bind(table.widthProperty().divide(4));
                    table.getColumns().add(vehicleIdColumn);

                    TableColumn<Schedule, LocalDateTime> departureTimeColumn = new TableColumn<>("Departure Time");
                    departureTimeColumn.setCellValueFactory(new PropertyValueFactory<Schedule, LocalDateTime>("departureTime"));
                    departureTimeColumn.setPrefWidth(tableWidth / 4);
                    //departureTimeColumn.prefWidthProperty().bind(table.widthProperty().divide(4));
                    table.getColumns().add(departureTimeColumn);

                    TableColumn<Schedule, LocalDateTime> arrivalTimeColumn = new TableColumn<>("Arrival Time");
                    arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Schedule, LocalDateTime>("arrivalTime"));
                    arrivalTimeColumn.setPrefWidth(tableWidth - (tableWidth / 4 * 3));
                    //arrivalTimeColumn.prefWidthProperty().bind(table.widthProperty().divide(4));
                    table.getColumns().add(arrivalTimeColumn);

                    VBox depotSchedule = new VBox(table);
                    depotSchedule.getStylesheets().add("styles/scheduleTable.css");

                    depotSchedule.setPrefWidth(this.width - menu.getWidth());
                    depotSchedule.setPrefHeight(this.height);
                    depotSchedule.setMaxWidth(this.width - menu.getWidth());
                    depotSchedule.setMaxHeight(this.height);
                    this.root.setCenter(depotSchedule);
                }
                else {
                    System.out.println(selectedItem.getValue());
                }
            }
        });
        try {
            menu.setStyle("-fx-background-color: #E7E7E7");
            menuList.getStylesheets().add("styles/treeView.css");
        } catch (NullPointerException e){
            System.out.println("NullPointerException: " + e);
        }

        this.root.setStyle("-fx-background-color: #DDDDDD");
    }

    public BorderPane getRoot(){
        return this.root;
    }

    public TreeItem<String> getUserInfoTreeItem(String username){
        List<Depot> userDepots = DepotsDAO.getDepotsOfUser(username);
        Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
        TreeItem<String> userItem = new TreeItem<>(username, new ImageView(userIcon));
        Image depotIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-depot-21.png")));
        for(Depot depot : userDepots) {
            TreeItem<String> userDepot = new TreeItem<String>("Depot: " + depot.getId(), new ImageView(depotIcon));
            userDepot.setExpanded(true);
            userItem.getChildren().add(userDepot); // temp

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
                        Image stationImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-train-station-19.png")));
                        TreeItem<String> routeStation = new TreeItem<String>(station.getPlacement(), new ImageView(stationImg));
                        routeStation.getGraphic().getStyleClass().add("stationRouteImg");
                        routeStation.setExpanded(true);
                        vehicleRoute.getChildren().add(routeStation); // temp
                    }
                }
                TreeItem<String> routes = new TreeItem<>("Routes");
            }
        }
        return userItem;
    }
}

