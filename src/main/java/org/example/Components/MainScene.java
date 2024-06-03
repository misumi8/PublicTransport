package org.example.Components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.example.ConnectionManager;
import org.example.DAOs.*;
import org.example.Entities.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class MainScene {
    private final BorderPane root;
    private final double width;
    private final double height;
    private final TreeItem<String> rootTreeNode;

    public MainScene(double width, double height){
        this.root = new BorderPane();
        this.width = width;
        this.height = height;

        Image logo = new Image("/icons/icon8-logo.png");
        ImageView logoView = new ImageView(logo);
        HBox logoBox = new HBox();
        logoBox.getChildren().add(logoView);
        HBox.setMargin(logoView, new Insets(70,0,0,-45));
        this.root.setCenter(logoBox);

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
        update.setOnMousePressed(mouseEvent -> {
            update.getStyleClass().add("pressed");
        });
        update.setOnMouseReleased(mouseEvent -> {
            update.getStyleClass().remove("pressed");
        });

        Button registerUser = new Button();
        registerUser.setOnAction(e-> {
            RegisterScene registerScene = new RegisterScene(this.width, this.height);
            registerScene.registerForm();
        });
        registerUser.setOnMousePressed(mouseEvent -> {
            registerUser.getStyleClass().add("pressed");
        });
        registerUser.setOnMouseReleased(mouseEvent -> {
            registerUser.getStyleClass().remove("pressed");
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
        menu.getStylesheets().add("/styles/menu.css");
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
            try{
                usersList.sort((a, b) -> {
                    return Math.toIntExact(a.getId() - b.getId());
                });
            }
            catch (NullPointerException exception){
                System.out.println("NullPointerException: (usersList sort)" + exception);
            }
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

        try{
            users.sort((a, b) -> {
                return Math.toIntExact(a.getId() - b.getId());
            });
        }
        catch (NullPointerException exception){
            System.out.println("NullPointerException: (usersList sort)" + exception);
        }

        for(User user : users) {
            Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
            TreeItem<String> userRoot = new TreeItem<>(user.getUsername(), new ImageView(userIcon));
            usersMap.put(user.getId(), userRoot.getValue());
            userRoot.setExpanded(true);
            rootTreeNode.getChildren().add(userRoot);
        }

        Set<String> vehicles = new HashSet<>();
        Set<String> stations = new HashSet<>();

        ContextMenu userContextMenu = new ContextMenu();
        MenuItem removeUser = new MenuItem("Remove user");
        userContextMenu.getItems().addAll(removeUser);
        removeUser.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                ConfirmationScene confirmationScene = new ConfirmationScene(this.width, this.height, selectedItem.getValue());
                confirmationScene.confirmationForm();
            }
        });

        ContextMenu depotContextMenu = new ContextMenu();
        MenuItem removeDepot = new MenuItem("Remove depot");
        MenuItem addDepot = new MenuItem("Add depot");
        depotContextMenu.getItems().addAll(addDepot, removeDepot);
        addDepot.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {

            }
        });
        removeDepot.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                DepotsDAO.deleteDepot(Long.parseLong(selectedItem.getValue().substring("Depot: ".length())));
            }
        });

        ContextMenu vehicleContextMenu = new ContextMenu();
        MenuItem removeVehicle = new MenuItem("Remove vehicle");
        MenuItem addVehicle = new MenuItem("Add vehicle");
        vehicleContextMenu.getItems().addAll(addVehicle, removeVehicle);
        addVehicle.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                VBox vehicleInfo = new VBox(10);
                // add new empty car to db
                VehiclesDAO.addNewCar(new Vehicle("NONE", null, null, null, ""));
                Image vehicleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-trolleybus-21.png")));
                TreeItem<String> newVehicle = new TreeItem<String>("NONE", new ImageView(vehicleIcon));
                //selectedItem.getParent().getChildren().add(newVehicle);
                displayVehicleInfo(newVehicle, menu, vehicleInfo, vehicles);
                vehicleInfo.setAlignment(Pos.TOP_CENTER);
                vehicleInfo.getStylesheets().add("/styles/vehicleInfo.css");
                vehicleInfo.getStyleClass().add("vehicleInfo");
                this.root.setCenter(vehicleInfo);
            }
        });
        removeVehicle.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                VehiclesDAO.deleteVehicle(selectedItem.getValue());
            }
        });

        ContextMenu routeContextMenu = new ContextMenu();
        MenuItem removeRoute = new MenuItem("Remove route");
        MenuItem addRoute = new MenuItem("Add route");
        routeContextMenu.getItems().addAll(addRoute, removeRoute);
        addRoute.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Route route = new Route(0, 0, 0);
                RoutesDAO.addRoute(route);
                displayRouteInfo(route, new TreeItem<String>("Route: Null"), menu);
            }
        });
        removeRoute.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                RoutesDAO.deleteRoute(Long.parseLong(selectedItem.getValue().substring("Route: ".length())));
            }
        });
        ContextMenu stationContextMenu = new ContextMenu();
        MenuItem removeStation = new MenuItem("Remove station");
        MenuItem addStation = new MenuItem("Add station");
        stationContextMenu.getItems().addAll(addStation, removeStation);
        removeStation.setOnAction(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                StationsDAO.deleteStation(selectedItem.getValue());
            }
        });

        menuList.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = menuList.getSelectionModel().getSelectedItem();
            if(selectedItem != null && selectedItem.getValue() != null) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    if(usersMap.containsValue(selectedItem.getValue()) || loggedUsers.contains(selectedItem.getValue())) {
                        userContextMenu.show(menuList, event.getScreenX(), event.getScreenY());
                        depotContextMenu.hide();
                        vehicleContextMenu.hide();
                        routeContextMenu.hide();
                        stationContextMenu.hide();
                    }
                    else if(selectedItem.getValue().startsWith("Depot: ")) {
                        depotContextMenu.show(menuList, event.getScreenX(), event.getScreenY());
                        userContextMenu.hide();
                        vehicleContextMenu.hide();
                        routeContextMenu.hide();
                        stationContextMenu.hide();
                    }
                    else if(vehicles.contains(selectedItem.getValue())) {
                        vehicleContextMenu.show(menuList, event.getScreenX(), event.getScreenY());
                        userContextMenu.hide();
                        depotContextMenu.hide();
                        routeContextMenu.hide();
                        stationContextMenu.hide();
                    }
                    else if(selectedItem.getValue().startsWith("Route:")) {
                        routeContextMenu.show(menuList, event.getScreenX(), event.getScreenY());
                        userContextMenu.hide();
                        depotContextMenu.hide();
                        vehicleContextMenu.hide();
                        stationContextMenu.hide();
                    }
                    else if(stations.contains(selectedItem.getValue())) {
                        stationContextMenu.show(menuList, event.getScreenX(), event.getScreenY());
                        userContextMenu.hide();
                        depotContextMenu.hide();
                        vehicleContextMenu.hide();
                        routeContextMenu.hide();
                    }
                }
                else {
                    userContextMenu.hide();
                    depotContextMenu.hide();
                    vehicleContextMenu.hide();
                    routeContextMenu.hide();
                    stationContextMenu.hide();
                    // user item:
                    if (!selectedItem.getValue().isEmpty() &&
                            usersMap.containsValue(selectedItem.getValue())) {
                        String username = selectedItem.getValue();
                        LoginScene loginScene = new LoginScene(this.width, this.height, username);
                        long userId = loginScene.loginForm();
                        if (userId > -1) {
                            usersMap.remove(userId);
                            List<Depot> userDepots = DepotsDAO.getDepotsOfUser(username);
                            Image userIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-user-21.png")));
                            Image depotIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-depot-21.png")));
                            for (Depot depot : userDepots) {
                                TreeItem<String> userDepot = new TreeItem<String>("Depot: " + depot.getId(), new ImageView(depotIcon));
                                userDepot.setExpanded(true);
                                selectedItem.getChildren().add(userDepot); // temp

                                List<Vehicle> depotVehicles = VehiclesDAO.getVehiclesOfDepot(depot.getId());
                                for (Vehicle vehicle : depotVehicles) {
                                    Image vehicleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icons8-trolleybus-21.png")));
                                    TreeItem<String> depotVehicle = new TreeItem<String>(vehicle.getPlate(), new ImageView(vehicleIcon));
                                    vehicles.add(depotVehicle.getValue());
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
                                            stations.add(routeStation.getValue());
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
                    else if (selectedItem.getValue().startsWith("Depot: ")) {
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
                    else if (vehicles.contains(selectedItem.getValue())) {
                        VBox vehicleInfo = new VBox(10);
                        displayVehicleInfo(selectedItem, menu, vehicleInfo, vehicles);
                        vehicleInfo.setAlignment(Pos.TOP_CENTER);
                        vehicleInfo.getStylesheets().add("/styles/vehicleInfo.css");
                        vehicleInfo.getStyleClass().add("vehicleInfo");
                        this.root.setCenter(vehicleInfo);
                    } else if (selectedItem.getValue().startsWith("Route:")) {
                        displayRouteInfo(null, selectedItem, menu);
                    } else if (stations.contains(selectedItem.getValue())) {
                        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_NONE));
                        WebView webView = new WebView();
                        WebEngine webEngine = webView.getEngine();
                        String url = "https://www.google.com/maps/search/" + selectedItem.getValue().replace(' ', '+');
                        webEngine.load(url);
                        webView.setPrefHeight(this.height);
                        webView.setPrefWidth(this.width - menu.getWidth());
                    /*ProgressIndicator progressIndicator = new ProgressIndicator();
                    progressIndicator.setMaxSize(100, 100);*/
                        this.root.setCenter(new StackPane(webView));
                    } else {
                        System.out.println(selectedItem.getValue());
                    }
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

    public void displayRouteInfo(Route route, TreeItem<String> selectedItem, BorderPane menu){
        Route selectedRoute = route == null ? RoutesDAO.getRoute(Long.parseLong(selectedItem.getValue().substring("Route: ".length()))) : route;
        if(route != null && !route.isRouteIdPresent()) route.setId(RoutesDAO.getEmptyRouteId());
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_NONE));
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        Map<Integer, String> routeStations = RoutesDAO.getStations(selectedItem.getValue().substring(7).toLowerCase().contains("null") ? RoutesDAO.getEmptyRouteId() : Long.parseLong(selectedItem.getValue().substring(7)));
        StringBuilder path = new StringBuilder();
        for (int i = 0; !routeStations.isEmpty(); ++i) {
            if (routeStations.containsKey(i)) {
                path.append(routeStations.get(i).replace(' ', '+')).append('/');
                routeStations.remove(i);
            }
        }
        String url = "https://www.google.com/maps/dir/" + path.toString();
        webEngine.load(url);
        webView.setPrefHeight(this.height * 0.7);
        webView.setMaxHeight(this.height * 0.7);
        webView.setPrefWidth(this.width - menu.getWidth());
        webView.getStylesheets().add("/styles/webView.css");

        Label idLabel = new Label("ID:");
        idLabel.setPadding(new Insets(2,0,0,0));
        Label ticketPrice = new Label("Ticket price:");
        VBox firstColLabels = new VBox(30);
        firstColLabels.getChildren().addAll(idLabel, ticketPrice);
        idLabel.setAlignment(Pos.CENTER);
        ticketPrice.setAlignment(Pos.CENTER);
        idLabel.setMaxWidth(Double.MAX_VALUE);
        ticketPrice.setMaxWidth(Double.MAX_VALUE);


        Label customers = new Label("Customers:");
        Label expTime = new Label("Expected time:");
        expTime.setPadding(new Insets(2,0,0,0));
        VBox secondColLabels = new VBox(30);
        secondColLabels.getChildren().addAll(expTime, customers);
        customers.setAlignment(Pos.CENTER);
        expTime.setAlignment(Pos.CENTER);
        expTime.setMaxWidth(Double.MAX_VALUE);
        customers.setMaxWidth(Double.MAX_VALUE);

        TextField idField = new TextField(!selectedRoute.isRouteIdPresent() ? RoutesDAO.getEmptyRouteId() + "" : selectedRoute.getId() + "");
        TextField ticketPriceField = new TextField(selectedRoute.getTicketPrice() + "");
        VBox firstColFields = new VBox(20);
        firstColFields.getChildren().addAll(idField, ticketPriceField);

        TextField customersField = new TextField(selectedRoute.getCostumers() + "");
        TextField expTimeField = new TextField(selectedRoute.getExpectedTime() + "");
        VBox secondColFields = new VBox(20);
        secondColFields.getChildren().addAll(expTimeField, customersField);

        HBox firstColumn = new HBox(10);
        firstColumn.getChildren().addAll(firstColLabels, firstColFields);
        firstColumn.setAlignment(Pos.CENTER);

        HBox secondColumn = new HBox(10);
        secondColumn.getChildren().addAll(secondColLabels, secondColFields);
        secondColumn.setAlignment(Pos.CENTER);

        HBox routeTextInfo = new HBox(100);
        routeTextInfo.getChildren().addAll(firstColumn, secondColumn);
        routeTextInfo.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Save ✔");
        saveButton.setAlignment(Pos.CENTER);

        VBox routeInfoAndButton = new VBox(10);
        VBox.setMargin(routeTextInfo, new Insets(-10,0,0,0));
        VBox.setMargin(saveButton, new Insets(5,0,0,0));
        saveButton.setOnMousePressed(mouseEvent -> {
            saveButton.getStyleClass().add("pressed");
        });
        saveButton.setOnMouseReleased(mouseEvent -> {
            saveButton.getStyleClass().remove("pressed");
            String idStr = idField.getText();
            String ticketPriceStr = ticketPriceField.getText();
            String customersStr = customersField.getText();
            String expTimeStr = expTimeField.getText();

            // Validation
            if (idStr.matches(".*\\D.*") || idStr.equals("0")) {
                Label errorMessage = new Label("The route ID must contain digits only and be greater than 0!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                routeInfoAndButton.getChildren().clear();
                routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton, errorMessage);
                idField.setStyle("-fx-background-color: #f78d8d;");
                idField.setText(selectedRoute.isRouteIdPresent() ? selectedRoute.getId() + "" : RoutesDAO.getEmptyRouteId() + "");
                idField.requestFocus();
            } else if (expTimeStr.matches(".*\\D.*") || expTimeStr.equals("0")){
                Label errorMessage = new Label("The expected time field must contain digits only and be greater than 0!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                routeInfoAndButton.getChildren().clear();
                routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton, errorMessage);
                expTimeField.setStyle("-fx-background-color: #f78d8d;");
                expTimeField.setText(selectedRoute.getExpectedTime() + "");
                expTimeField.requestFocus();
            } else if (ticketPriceStr.matches(".*\\D.*") || ticketPriceStr.equals("0")){
                Label errorMessage = new Label("The ticket price field must contain digits only and be greater than 0!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                routeInfoAndButton.getChildren().clear();
                routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton, errorMessage);
                ticketPriceField.setStyle("-fx-background-color: #f78d8d;");
                ticketPriceField.setText(selectedRoute.getTicketPrice() + "");
                ticketPriceField.requestFocus();
            } else if (customersStr.matches(".*\\D.*") || customersStr.equals("0")){
                Label errorMessage = new Label("The customer field must contain digits only and be greater than 0!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                routeInfoAndButton.getChildren().clear();
                routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton, errorMessage);
                customersField.setStyle("-fx-background-color: #f78d8d;");
                customersField.setText(selectedRoute.getCostumers() + "");
                customersField.requestFocus();
            } // Validation passed:
            else {
                try (Connection connection = ConnectionManager.getConnection()) {
                    Label successMessage = new Label("Success:");
                    successMessage.setStyle("-fx-text-fill: #099e02;");
                    successMessage.setAlignment(Pos.CENTER);
                    if (!idStr.equals(selectedRoute.getId() + "")) {
                        System.out.println("Id mod");
                        PreparedStatement preparedStatement = connection.prepareStatement("update routes set id = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(idStr));
                        preparedStatement.setLong(2, !selectedRoute.isRouteIdPresent() ? RoutesDAO.getEmptyRouteId() : selectedRoute.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Route ID");
                        else successMessage.setText(successMessage.getText() + ", Route ID");
                    }
                    if (!customersStr.equals(selectedRoute.getCostumers() + "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update routes set customers = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(customersStr));
                        preparedStatement.setLong(2, !selectedRoute.isRouteIdPresent() ? RoutesDAO.getEmptyRouteId() : selectedRoute.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Customers");
                        else successMessage.setText(successMessage.getText() + ", Customers");
                    }
                    if (!ticketPriceStr.equals(selectedRoute.getTicketPrice() + "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update routes set ticket_price = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(ticketPriceStr));
                        preparedStatement.setLong(2, !selectedRoute.isRouteIdPresent() ? RoutesDAO.getEmptyRouteId() : selectedRoute.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Ticket price");
                        else successMessage.setText(successMessage.getText() + ", Ticket price");
                    }
                    if (!expTimeStr.equals(selectedRoute.getExpectedTime() + "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update routes set expected_time = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(expTimeStr));
                        preparedStatement.setLong(2, !selectedRoute.isRouteIdPresent() ? RoutesDAO.getEmptyRouteId() : selectedRoute.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Expected time");
                        else successMessage.setText(successMessage.getText() + ", Expected time");
                    }
                    if (!successMessage.getText().equals("Success:")) {
                        routeInfoAndButton.getChildren().clear();
                        routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton, successMessage);
                    }
                } catch (SQLException e) {
                    System.out.println("SQLException: (updateRouteButton)" + e);
                    if (e.getLocalizedMessage().startsWith("ORA-00001")) {
                        Label errorMessage = new Label("Another route is using the same route identification number!");
                        errorMessage.setStyle("-fx-text-fill: red;");
                        errorMessage.setAlignment(Pos.CENTER);
                        routeInfoAndButton.getChildren().clear();
                        routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton, errorMessage);
                    }
                }
            }
        });
        routeInfoAndButton.setPadding(new Insets(-15, 0,0,0));
        routeInfoAndButton.setAlignment(Pos.CENTER);
        routeInfoAndButton.setPrefHeight(this.height - webView.getHeight());
        routeInfoAndButton.setPrefWidth(this.width - menu.getWidth());
        routeInfoAndButton.getChildren().addAll(routeTextInfo, saveButton);
        routeInfoAndButton.getStylesheets().add("/styles/routeInfo.css");
        routeInfoAndButton.getStyleClass().add("routeInfo");

        BorderPane routeInfo = new BorderPane();
        routeInfo.setPrefHeight(this.height);
        routeInfo.setPrefWidth(this.width - menu.getWidth());
        routeInfo.setTop(webView);
        routeInfo.setCenter(routeInfoAndButton);

        this.root.setCenter(routeInfo);
    }

    public void displayVehicleInfo(TreeItem<String> selectedItem, BorderPane menu, VBox vehicleInfo, Set<String> vehicles){
        //Vehicle vehicle = VehiclesDAO.getVehicle(selectedItem.getValue());
        Vehicle selectedVehicle = VehiclesDAO.getVehicle(selectedItem.getValue());
        //System.out.println("selectedItem.getValue: " + selectedItem.getValue() + " || " + selectedVehicle.getId());
        Image vehicleImg = null;
        if(selectedVehicle != null) {
            if (selectedVehicle.getType().isEmpty()) {
                vehicleImg = new Image("/icons/unknownType.png");
            } else if (selectedVehicle.getType().equals("BUS")) {
                vehicleImg = new Image("/icons/bus.png");
            } else if (selectedVehicle.getType().equals("TROLLEYBUS")) {
                vehicleImg = new Image("/icons/trolleybus.png");
            } else if (selectedVehicle.getType().equals("TRAM")) {
                vehicleImg = new Image("/icons/tram.png");
            }
        }
        else vehicleImg = new Image("/icons/unknownType.png");
        ImageView vehicleImageView = new ImageView(vehicleImg);
        vehicleImageView.setFitHeight((this.width - menu.getWidth()) * 0.45);
        vehicleImageView.setFitWidth((this.width - menu.getWidth()) * 0.85);

        // Vehicle Info:
        HBox vehicleModifiableInfo = new HBox(80);
        vehicleModifiableInfo.setAlignment(Pos.CENTER);
        vehicleModifiableInfo.setPadding(new Insets(55, 0, 0, 0));

        HBox idPlate = new HBox(50);
        idPlate.setAlignment(Pos.CENTER);

        VBox idPlateLabels = new VBox(22);
        idPlateLabels.getStyleClass().add("dataInfoType");
        Label id = new Label("ID:");
        id.setPadding(new Insets(2, 0, 0, 0));
        id.setAlignment(Pos.CENTER);
        Label plate = new Label("Plate:");
        plate.setAlignment(Pos.CENTER);
        id.setMaxWidth(Double.MAX_VALUE);
        plate.setMaxWidth(Double.MAX_VALUE);
        idPlateLabels.getChildren().addAll(id, plate);

        VBox idPlateFields = new VBox(10);
        TextField idField = new TextField(selectedVehicle != null ? selectedVehicle.getId() + "" : "");
        TextField plateField = new TextField(selectedItem.getValue());
        idPlateFields.getChildren().addAll(idField, plateField);
        idPlate.getChildren().addAll(idPlateLabels, idPlateFields);
        //
        HBox dateType = new HBox(50);
        dateType.setAlignment(Pos.CENTER);

        VBox dateTypeLabels = new VBox(25);
        dateTypeLabels.getStyleClass().add("dataInfoType");
        Label date = new Label("Date of manufacture:");
        date.setPadding(new Insets(1, 0, 0, 0));
        date.setAlignment(Pos.CENTER);
        Label type = new Label("Type:");
        type.setAlignment(Pos.CENTER);
        date.setMaxWidth(Double.MAX_VALUE);
        type.setMaxWidth(Double.MAX_VALUE);
        dateTypeLabels.getChildren().addAll(date, type);

        VBox dateTypeFields = new VBox(10);
        DatePicker dateField = new DatePicker();
        // convert Date to LocalDate:
        dateField.setValue(!selectedVehicle.vehicleHasNullValue() ? Instant.ofEpochMilli(selectedVehicle.getDateOfManufacture().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate() : LocalDate.of(2000, 1, 1));
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("TROLLEYBUS", "TRAM", "BUS");
        typeComboBox.setValue(selectedVehicle.getType());
        dateField.setPrefWidth(this.width * 0.1);
        typeComboBox.setPrefWidth(this.width * 0.1);
        dateTypeFields.getChildren().addAll(dateField, typeComboBox);
        dateType.getChildren().addAll(dateTypeLabels, dateTypeFields);
        //
        HBox depotRoute = new HBox(50);
        depotRoute.setAlignment(Pos.CENTER);

        VBox depotRouteLabels = new VBox(22);
        depotRouteLabels.getStyleClass().add("dataInfoType");
        Label depot = new Label("Depot:");
        depot.setPadding(new Insets(2, 0, 0, 0));
        depot.setAlignment(Pos.CENTER);
        Label route = new Label("Route:");
        route.setAlignment(Pos.CENTER);
        depot.setMaxWidth(Double.MAX_VALUE);
        route.setMaxWidth(Double.MAX_VALUE);
        depotRouteLabels.getChildren().addAll(depot, route);

        VBox depotRouteFields = new VBox(10);
        TextField depotField = new TextField(selectedVehicle != null ? selectedVehicle.getDepotId() + "" : "");
        TextField routeField = new TextField(selectedVehicle != null ? selectedVehicle.getRouteId() + "" : "");
        depotRouteFields.getChildren().addAll(depotField, routeField);
        depotRoute.getChildren().addAll(depotRouteLabels, depotRouteFields);
        vehicleModifiableInfo.getChildren().addAll(idPlate, dateType, depotRoute);
        //
        Button save = new Button("Save ✔");
        save.setPrefHeight(this.height * 0.05);
        //save.setPrefWidth(this.width * 0.05);
        VBox.setMargin(save, new Insets(40, 0, 0, 0));

        save.setOnMousePressed(mouseEvent -> {
            save.getStyleClass().add("pressed");
        });

        save.setOnMouseReleased(mouseEvent -> {
            save.getStyleClass().remove("pressed");
            String idText = idField.getText();
            String plateText = plateField.getText();
            LocalDate dateValue = dateField.getValue();
            String typeComboValue = typeComboBox.getValue();
            String depotIdValue = depotField.getText();
            String routeIdValue = routeField.getText();

            // Validation
            if (idText.matches(".*\\D.*")) {
                Label errorMessage = new Label("The vehicle ID must contain digits only!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                vehicleInfo.getChildren().clear();
                vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, errorMessage);
                idField.setStyle("-fx-background-color: #f78d8d;");
                idField.setText(selectedVehicle != null ? selectedVehicle.getId() + "" : "");
                idField.requestFocus();
            } else if (plateText.length() > 11) {
                Label errorMessage = new Label("The vehicle plate can be a maximum 11 characters long!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                vehicleInfo.getChildren().clear();
                vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, errorMessage);
                plateField.setStyle("-fx-background-color: #f78d8d;");
                plateField.setText(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getPlate() : "");
                plateField.requestFocus();
            } else if (plateText.toCharArray()[2] != ' ' || plateText.toCharArray()[5] != ' '){
                Label errorMessage = new Label("The vehicle plate should be in the format \"XX XX XXX\"");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                vehicleInfo.getChildren().clear();
                vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, errorMessage);
                plateField.setStyle("-fx-background-color: #f78d8d;");
                plateField.setText(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getPlate() : "");
                plateField.requestFocus();
            } else if (depotIdValue.matches(".*\\D.*")) {
                Label errorMessage = new Label("The depot ID must contain digits only!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                vehicleInfo.getChildren().clear();
                vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, errorMessage);
                depotField.setStyle("-fx-background-color: #f78d8d;");
                depotField.setText(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getDepotId() + "" : "");
                depotField.requestFocus();
            } else if (routeIdValue.matches(".*\\D.*")) {
                Label errorMessage = new Label("The route ID must contain digits only!");
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setAlignment(Pos.CENTER);
                vehicleInfo.getChildren().clear();
                vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, errorMessage);
                routeField.setStyle("-fx-background-color: #f78d8d;");
                routeField.setText(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getRouteId() + "" : "");
                routeField.requestFocus();
            }
            // validation passed:
            else {
                try (Connection connection = ConnectionManager.getConnection()) {
                    Label successMessage = new Label("Success:");
                    successMessage.setStyle("-fx-text-fill: #099e02;");
                    successMessage.setAlignment(Pos.CENTER);
                    if (!routeIdValue.equals(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getRouteId() + "" : "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set route_id = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(routeIdValue));
                        preparedStatement.setLong(2, selectedVehicle.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Route ID");
                        else successMessage.setText(successMessage.getText() + ", Route ID");
                    }
                    if (!depotIdValue.equals(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getDepotId() + "" : "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set depot_id = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(depotIdValue));
                        preparedStatement.setLong(2, selectedVehicle.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Depot ID");
                        else successMessage.setText(successMessage.getText() + ", Depot ID");
                    }
                    if (!typeComboValue.equals(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getType() : "") && !typeComboValue.equals("NONE")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set type = ? where id = ?");
                        preparedStatement.setString(1, typeComboValue);
                        preparedStatement.setLong(2, selectedVehicle.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Type");
                        else successMessage.setText(successMessage.getText() + ", Type");
                    }
                    if (!dateValue.equals(!selectedVehicle.vehicleHasNullValue() ? Instant.ofEpochMilli(selectedVehicle.getDateOfManufacture().getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate() : LocalDate.of(2000,1,1))) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set date_of_manufacture = ? where id = ?");
                        preparedStatement.setDate(1, java.sql.Date.valueOf(dateValue));
                        preparedStatement.setLong(2, selectedVehicle.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Date of manufacture");
                        else successMessage.setText(successMessage.getText() + ", Date of manufacture");
                    }
                    if (!plateText.equals(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getPlate() : "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set plate = ? where id = ?");
                        preparedStatement.setString(1, plateText);
                        preparedStatement.setLong(2, selectedVehicle.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        vehicles.remove(selectedItem.getValue());
                        vehicles.add(plateText);
                        selectedItem.setValue(plateText);
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " Plate");
                        else successMessage.setText(successMessage.getText() + ", Plate");
                    }
                    if (!idText.equals(!selectedVehicle.vehicleHasNullValue() ? selectedVehicle.getId() + "" : "")) {
                        PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set id = ? where id = ?");
                        preparedStatement.setLong(1, Long.parseLong(idText));
                        preparedStatement.setLong(2, selectedVehicle.getId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        if (successMessage.getText().equals("Success:"))
                            successMessage.setText(successMessage.getText() + " ID");
                        else successMessage.setText(successMessage.getText() + ", ID");
                    }
                    if (!successMessage.getText().equals("Success:")) {
                        vehicleInfo.getChildren().clear();
                        vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, successMessage);
                    }
                } catch (SQLException e) {
                    System.out.println("SQLException: (updateVehicleInfoButton)" + e);
                    if (e.getLocalizedMessage().startsWith("ORA-00001")) {
                        Label errorMessage = new Label("Another vehicle is using the same vehicle identification number or plate!");
                        errorMessage.setStyle("-fx-text-fill: red;");
                        errorMessage.setAlignment(Pos.CENTER);
                        vehicleInfo.getChildren().clear();
                        vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save, errorMessage);
                    }
                }
            }
        });

        //save.setPadding(new Insets(20,0,0,0));
        vehicleInfo.getChildren().addAll(vehicleImageView, vehicleModifiableInfo, save);
        vehicleInfo.setPadding(new Insets(-35, 0, 0, 0));
        vehicleInfo.setPrefWidth(this.width - menu.getWidth());
        vehicleInfo.setPrefHeight(this.height);
        vehicleInfo.setMaxWidth(this.width - menu.getWidth());
        vehicleInfo.setMaxHeight(this.height);
    }

}

