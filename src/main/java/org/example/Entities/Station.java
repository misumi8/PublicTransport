package org.example.Entities;

public class Station {
    private Long id;
    private String placement;
    private Long routeId;
    private int orderNumber;

    public Station(Long id, String placement, Long routeId, int orderNumber) {
        this.id = id;
        this.placement = placement;
        this.routeId = routeId;
        this.orderNumber = orderNumber;
    }

    public Station(String placement, Long routeId, int orderNumber) {
        this.placement = placement;
        this.routeId = routeId;
        this.orderNumber = orderNumber;
    }

    public Station() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", placement='" + placement + '\'' +
                ", routeId=" + routeId +
                ", orderNumber=" + orderNumber +
                '}';
    }
}
