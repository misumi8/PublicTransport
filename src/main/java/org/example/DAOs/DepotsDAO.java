package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Depot;
import org.example.Entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepotsDAO {
    public static List<Depot> getDepotsOfUser(String username){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement userIdStatement = connection.prepareStatement("select id from users u where u.username = (?)");
            userIdStatement.setString(1, username);
            ResultSet userIdResult = userIdStatement.executeQuery();
            long userId = -1L;
            if(userIdResult.next()) userId = userIdResult.getLong("id");
            System.out.println("userId = " + userId);
            Statement statement = connection.createStatement();
            assert userId >= 0;
            ResultSet result = statement.executeQuery("select * from depots d where d.user_id = " + userId);
            List<Depot> userDepots = new ArrayList<>();
            while(result.next()) {
                userDepots.add(new Depot(result.getLong("id"),
                        result.getString("placement"),
                        result.getLong("user_id")));
            }
            result.close();
            statement.close();
            connection.close();
            return userDepots;
        }
        catch (SQLException e){
            System.out.println("SQLException (depotsDAO): " + e);
        }
        catch (NullPointerException e){
            System.out.println("NullPointerException: " + e);
        }
        return null;
    }

    public static List<Depot> getDepotsOfUser(Long userId){
        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from depots d where d.user_id = " + userId);
            List<Depot> userDepots = new ArrayList<>();
            while(result.next()) {
                userDepots.add(new Depot(result.getLong("id"),
                        result.getString("placement"),
                        result.getLong("user_id")));
            }
            result.close();
            statement.close();
            connection.close();
            return userDepots;
        }
        catch (SQLException e){
            System.out.println("SQLException (depotsDAO): " + e);
        }
        catch (NullPointerException e){
            System.out.println("NullPointerException: " + e);
        }
        return null;
    }

    public static void deleteDepot(Long depotId){
        try(Connection connection = ConnectionManager.getConnection()){
            ScheduleDAO.deleteScheduleRecord(depotId);
            VehiclesDAO.deleteVehiclesFromDepot(depotId);
            PreparedStatement preparedStatement = connection.prepareStatement("delete depots where id = ?");
            preparedStatement.setLong(1, depotId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: (deleteDepot) " + e);
        }
    }
}
