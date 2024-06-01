package org.example.DAOs;

import org.example.ConnectionManager;
import org.example.Entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {
    public static void addUsers(){
        Connection connection = ConnectionManager.getConnection();
    }

    public static List<User> getUsers(){
        try(Connection connection = ConnectionManager.getConnection()){
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

    public static String tryRegister(String username, String password, String city){
        try(Connection connection = ConnectionManager.getConnection()){
            CallableStatement callableStatement = connection.prepareCall("{ ? = call registerUser(?,?,?) }");
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.setString(2, username);
            callableStatement.setString(3, password);
            callableStatement.setString(4, city);
            callableStatement.execute();
            int result = callableStatement.getInt(1);
            callableStatement.close();
            connection.close();
            return null;
        }
        catch (SQLException e){
            System.out.println("SQLException (tryRegister):" + e);
            return e.getLocalizedMessage();
        }
    }

    public static long tryLogin(String username, String password) {
        try(Connection connection = ConnectionManager.getConnection()){
            CallableStatement callableStatement = connection.prepareCall("{ ? = call login(?,?) }");
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.setString(2, username);
            callableStatement.setString(3, password);
            callableStatement.execute();
            long result = callableStatement.getLong(1);
            callableStatement.close();
            connection.close();
            return result;
        }
        catch (SQLException e){
            System.out.println("SQLException (tryLogin):" + e);
        }
        return -1;
    }
}
