package org.example.Entities;

public class Depot {
    private Long id;
    private String placement;
    private Long userId;

    public Depot(Long id, String placement, Long userId) {
        this.id = id;
        this.placement = placement;
        this.userId = userId;
    }

    public Depot(String placement, Long userId) {
        this.placement = placement;
        this.userId = userId;
    }

    public Depot() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    @Override
    public String toString() {
        return "Depot{" +
                "id=" + id +
                ", placement='" + placement + '\'' +
                '}';
    }
}
