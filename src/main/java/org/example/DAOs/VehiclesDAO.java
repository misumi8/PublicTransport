package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Depot;
import org.example.Entities.Vehicle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VehiclesDAO {
    public static List<Vehicle> getVehiclesOfDepot(Long depotId) {
        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from vehicles d where d.depot_id = " + depotId);
            List<Vehicle> depotVehicles = new ArrayList<>();
            while (result.next()) {
                depotVehicles.add(new Vehicle(result.getLong("id"),
                            result.getString("plate"),
                            result.getDate("date_of_manufacture"),
                            result.getLong("depot_id"),
                            result.getLong("route_id")));
            }
            result.close();
            statement.close();
            connection.close();
            return depotVehicles;
        } catch (SQLException e) {
            System.out.println("SQLException (vehiclesDAO): " + e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e);
        }
        return null;
    }
}
