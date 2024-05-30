package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {
    public static void addUsers(){
        Connection connection = ConnectionManager.getConnection();
    }

    public static List<User> getUsers(){
        try{
            Connection connection = ConnectionManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from users");
            List<User> users = new ArrayList<>();
            while(result.next()) {
                users.add(new User(result.getLong("id"),
                        result.getString("username"),
                        result.getString("city")));
            }
            result.close();
            statement.close();
            connection.close();
            return users;
        }
        catch (SQLException e){
            System.out.println("SQLException (usersDAO): " + e);
        }
        catch (NullPointerException e){
            System.out.println("NullPointerException: " + e);
        }
        return null;
    }
}
