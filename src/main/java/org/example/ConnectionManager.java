package org.example;

import com.zaxxer.hikari.*;
import java.sql.*;

public class ConnectionManager {
    //private static final HikariConfig hikariConfig = new HikariConfig("src/main/java/hikari.properties");
    private static final HikariDataSource hikariDataSource = new HikariDataSource(new HikariConfig("src/main/resources/hikari.properties"));
    private ConnectionManager() {}

    public static Connection getConnection(){
        try {
            // .setAutoCommit(false) => auto-commit disabled
            return hikariDataSource.getConnection();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
        return null;
    }

    public static void closeConnection(Connection connectionToClose){
        try{
            connectionToClose.close();
        }
        catch (SQLException e){
            System.out.println("SQLException: " + e);
        }
    }

    /*public static void displayBooksContent(Statement statement){
        try{
            ResultSet resultSet = statement.executeQuery("select * from books");
            System.out.println("\nid" + " | " + "      title      " + " | " + "language" + " | " + " pub_date " + " | " + "pages");
            while (resultSet.next()){
                System.out.println(' ' + resultSet.getString("id") + " | " + resultSet.getString("title") + " | " + resultSet.getString("language") + " | " + resultSet.getString("pub_date") + " | " + resultSet.getString("pages"));
            }
        }
        catch(SQLException e){
            System.out.println("SQLException: " + e);
        }
    }*/
}
