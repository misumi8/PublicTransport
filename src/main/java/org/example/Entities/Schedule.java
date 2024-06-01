package org.example.Entities;

import org.example.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Schedule {
    private Long depotId;
    private Long vehicleId;
    private Long routeId;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public Schedule(Long depotId, Long vehicleId, Timestamp departureTime) {
        this.depotId = depotId;
        this.vehicleId = vehicleId;
        this.departureTime = departureTime.toLocalDateTime().toLocalTime();
        try {
            Connection connection = ConnectionManager.getConnection();
            CallableStatement callableStatement = connection.prepareCall("{ ? = call getArrivalTime(?) }");
            callableStatement.registerOutParameter(1, Types.TIMESTAMP);
            callableStatement.setLong(2, vehicleId);
            callableStatement.execute();
            this.arrivalTime = callableStatement.getTimestamp(1).toLocalDateTime().toLocalTime();
            callableStatement.close();
            connection.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
    }

    public Schedule() {}

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "depotId=" + depotId +
                ", vehicleId=" + vehicleId +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
