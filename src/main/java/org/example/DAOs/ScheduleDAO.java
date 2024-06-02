package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Schedule;
import org.example.Entities.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    public static List<Schedule> getAllData(Long depotId) {
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("select * from schedule s where s.depot_id = (?)");
            preparedStatement.setLong(1, depotId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Schedule> schedules = new ArrayList<>();
            while(resultSet.next()){
                schedules.add(new Schedule(resultSet.getLong("depot_id"),
                        resultSet.getLong("vehicle_id"),
                        resultSet.getTimestamp("departure_time")));
            }
            return schedules;
        }
        catch (SQLException e){
            System.out.println("SQLException: (getAllData) " + e);
        }
        return null;
    }

    public static void deleteScheduleRecord(Long depotId){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("delete schedule where depot_id = ?");
            preparedStatement.setLong(1, depotId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
    }

    public static void deleteScheduleRecordByVehicle(String plate){
        try(Connection connection = ConnectionManager.getConnection()){
            Vehicle vehicle = VehiclesDAO.getVehicle(plate);
            PreparedStatement preparedStatement = connection.prepareStatement("delete schedule where vehicle_id = ?");
            preparedStatement.setLong(1, vehicle.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
    }

}
