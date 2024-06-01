package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Route;
import org.example.Entities.Station;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StationsDAO {
    public static List<Station> getStationsOfRoute(Long routeId) {
        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from stations s where s.route_id = " + routeId);
            List<Station> stations = new ArrayList<>();
            while (result.next()) {
                stations.add(new Station(result.getLong("id"),
                        result.getString("placement"),
                        result.getLong("route_id"),
                        result.getInt("order_number")));
            }
            result.close();
            statement.close();
            connection.close();
            return stations;
        } catch (SQLException e) {
            System.out.println("SQLException (stationsDAO): " + e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e);
        }
        return null;
    }
}
