package org.example.Entities;

import java.util.Date;

public class Vehicle {
    private Long id;
    private String plate;
    private Date dateOfManufacture;
    private Long depotId;
    private Long routeId;

    public Vehicle(Long id, String plate, Date dateOfManufacture, Long depotId, Long routeId) {
        this.id = id;
        this.plate = plate;
        this.dateOfManufacture = dateOfManufacture;
        this.depotId = depotId;
        this.routeId = routeId;
    }

    public Vehicle(String plate, Date dateOfManufacture, Long depotId, Long routeId) {
        this.plate = plate;
        this.dateOfManufacture = dateOfManufacture;
        this.depotId = depotId;
        this.routeId = routeId;
    }

    public Vehicle() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Date getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(Date dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", plate='" + plate + '\'' +
                ", dateOfManufacture=" + dateOfManufacture +
                ", depotId=" + depotId +
                ", routeId=" + routeId +
                '}';
    }
}
