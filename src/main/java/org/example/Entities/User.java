package org.example.Entities;

public class User {
    private Long id;
    private String username;
    private String city;

    public User(String username, String city) {
        this.username = username;
        this.city = city;
    }

    public User(Long id, String username, String city) {
        this.id = id;
        this.username = username;
        this.city = city;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
