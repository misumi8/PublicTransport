package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Route;
import org.example.Entities.Station;
import org.example.Entities.Vehicle;

import java.sql.*;
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

    public static void deleteStationByRouteId(Long routeId){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("delete stations where route_id = ?");
            preparedStatement.setLong(1, routeId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
    }

    public static void deleteStation(String placement){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("delete stations where placement = ?");
            preparedStatement.setString(1, placement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
    }
}
