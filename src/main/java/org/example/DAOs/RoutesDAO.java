package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Route;
import org.example.Entities.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutesDAO {
    public static Map<Integer,String> getStations(Long routeId){
        Map<Integer,String> stations = new HashMap<>();
        try (Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("select * from stations where route_id = ?");
            preparedStatement.setLong(1, routeId);
            ResultSet vehicleResult = preparedStatement.executeQuery();
                while (vehicleResult.next()) {
                    stations.put(vehicleResult.getInt("order_number"), vehicleResult.getString("placement"));
                }
        } catch (SQLException e) {
            System.out.println("SQLException (routesDAO): " + e); /// probbbbbleeeeeemsssss
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e);
        }
        return stations;
    }
    public static List<Route> getRoutesOfVehicle(Long vehicleId) {
        List<Route> routes = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement vehicleStatement = connection.prepareStatement("select * from vehicles where id = ?");
             PreparedStatement routeStatement = connection.prepareStatement("select * from routes where id = ?")) {

            vehicleStatement.setLong(1, vehicleId);
            try (ResultSet vehicleResult = vehicleStatement.executeQuery()) {
                while (vehicleResult.next()) {
                    long routeId = vehicleResult.getLong("route_id");
                    routeStatement.setLong(1, routeId);
                    try (ResultSet routeResult = routeStatement.executeQuery()) {
                        if (routeResult.next()) {
                            routes.add(new Route(routeResult.getLong("id"),
                                    routeResult.getInt("ticket_price"),
                                    routeResult.getInt("customers"),
                                    routeResult.getInt("expected_time")));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException (routesDAO): " + e); /// probbbbbleeeeeemsssss
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e);
        }
        return routes;
    }

    public static void deleteRoute(Long routeId){
        VehiclesDAO.setRouteNull(routeId);
        StationsDAO.deleteStationByRouteId(routeId);
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("delete routes where id = ?");
            preparedStatement.setLong(1, routeId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: (deleteVehiclesFromDepot) " + e);
        }
    }
}
