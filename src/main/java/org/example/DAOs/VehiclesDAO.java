package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.Depot;
import org.example.Entities.Vehicle;

import java.sql.*;
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

    public static Vehicle getVehicle(String plate){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("select * from vehicles v where v.plate = ?");
            preparedStatement.setString(1, plate);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()) {
                Vehicle vehicle = new Vehicle(result.getLong("id"),
                        result.getString("plate"),
                        result.getDate("date_of_manufacture"),
                        result.getLong("depot_id"),
                        result.getLong("route_id"),
                        result.getString("type"));
                result.close();
                preparedStatement.close();
                connection.close();
                return vehicle;
            }
            result.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException (vehiclesDAO): " + e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e);
        }
        return null;
    }

    public static void deleteVehicle(String plate){
        ScheduleDAO.deleteScheduleRecordByVehicle(plate);
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("delete vehicles where plate = ?");
            preparedStatement.setString(1, plate);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: (deleteVehicle) " + e);
        }
    }

    public static void deleteVehiclesFromDepot(Long depotId){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("delete vehicles where depot_id = ?");
            preparedStatement.setLong(1, depotId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: (deleteVehiclesFromDepot) " + e);
        }
    }

    public static void setRouteNull(Long routeId){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("update vehicles set route_id = NULL where route_id = ?");
            preparedStatement.setLong(1, routeId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: (deleteVehiclesFromDepot) " + e);
        }
    }

    public static void addNewCar(Vehicle vehicle){
        try(Connection connection = ConnectionManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("insert into vehicles (plate,date_of_manufacture,depot_id,route_id,type) values (?,?,?,?,?)");
            if(vehicle.getPlate() == null) preparedStatement.setNull(1, Types.VARCHAR);
            else preparedStatement.setString(1, vehicle.getPlate());
            if(vehicle.getDateOfManufacture() == null) preparedStatement.setNull(2, Types.DATE);
            else preparedStatement.setDate(2, vehicle.getDateOfManufacture());
            if(vehicle.getDepotId() == null) preparedStatement.setNull(3, Types.INTEGER);
            else preparedStatement.setLong(3, vehicle.getDepotId());
            if(vehicle.getRouteId() == null) preparedStatement.setNull(4, Types.INTEGER);
            else preparedStatement.setLong(4, vehicle.getRouteId());
            if(vehicle.getType() == null) preparedStatement.setNull(5, Types.VARCHAR);
            else preparedStatement.setString(5, vehicle.getType());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: (deleteVehiclesFromDepot) " + e);
        }
    }
}
