package org.example.Entities;

public class Route {
    private Long id;
    private int ticketPrice;
    private int costumers;
    private int expectedTime;

    public Route(Long id, int ticketPrice, int costumers, int expectedTime) {
        this.id = id;
        this.ticketPrice = ticketPrice;
        this.costumers = costumers;
        this.expectedTime = expectedTime;
    }

    public Route(int ticketPrice, int costumers, int expectedTime) {
        this.ticketPrice = ticketPrice;
        this.costumers = costumers;
        this.expectedTime = expectedTime;
    }

    public Route() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getCostumers() {
        return costumers;
    }

    public void setCostumers(int costumers) {
        this.costumers = costumers;
    }

    public int getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(int expectedTime) {
        this.expectedTime = expectedTime;
    }

    public boolean isRouteIdPresent(){
        //System.out.println("isRouteIdPresent: " + this.id);
        return this.id != null;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", ticketPrice=" + ticketPrice +
                ", costumers=" + costumers +
                ", expectedTime=" + expectedTime +
                '}';
    }
}
