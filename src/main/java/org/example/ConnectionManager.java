package org.example;

import com.zaxxer.hikari.*;
import java.sql.*;

public class ConnectionManager {
    //private static final HikariConfig hikariConfig = new HikariConfig("src/main/java/hikari.properties");
    private static final HikariDataSource hikariDataSource = new HikariDataSource(new HikariConfig("src/main/resources/hikari.properties"));
    private ConnectionManager() {}

    public static Connection getConnection(){
        try {
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
}
