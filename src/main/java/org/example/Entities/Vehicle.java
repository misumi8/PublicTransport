package org.example.Entities;

import java.sql.Date;

public class Vehicle {
    private Long id;
    private String plate;
    private Date dateOfManufacture;
    private Long depotId;
    private Long routeId;
    private String type;

    public Vehicle(Long id, String plate, Date dateOfManufacture, Long depotId, Long routeId, String type) {
        this.id = id;
        this.plate = plate != null ? plate : "";
        this.dateOfManufacture = dateOfManufacture;
        this.depotId = depotId;
        this.routeId = routeId;
        this.type = type != null ? type : "";
    }

    public Vehicle(Long id, String plate, Date dateOfManufacture, Long depotId, Long routeId) {
        this.id = id;
        this.plate = plate;
        this.dateOfManufacture = dateOfManufacture;
        this.depotId = depotId;
        this.routeId = routeId;
    }

    public Vehicle(String plate, Date dateOfManufacture, Long depotId, Long routeId, String type) {
        this.plate = plate;
        this.dateOfManufacture = dateOfManufacture;
        this.depotId = depotId;
        this.routeId = routeId;
    }

    public Vehicle() {
        this.type = "";
        this.plate = "";

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public boolean vehicleHasNullValue(){
        return this.plate == null ||
                this.id == null ||
                this.depotId == null ||
                this.routeId == null ||
                this.dateOfManufacture == null;
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
