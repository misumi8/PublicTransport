package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Route;
import org.example.Entities.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoutesDAO {
    public static List<Route> getRoutesOfVehicle(Long vehicleId) {
        List<Route> routes = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement vehicleStatement = connection.prepareStatement("SELECT * FROM vehicles WHERE id = ?");
             PreparedStatement routeStatement = connection.prepareStatement("SELECT * FROM routes WHERE id = ?")) {

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

}
