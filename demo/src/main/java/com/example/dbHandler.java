package com.example;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class dbHandler {
    private static final String CON_STR = "jdbc:sqlite:db/test.db";

    private static dbHandler instance = null;

    public static synchronized dbHandler getInstance() throws SQLException{
        if (instance == null){
            instance = new dbHandler();
        }

        return instance;
    }

    private Connection connection;

    private dbHandler() throws SQLException{
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR);

        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS states ("
            + " id         INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " EDUGroup   TEXT NOT NULL,"
            + " weak       INTEGER,"
            + " day        INTEGER NOT NULL,"
            + " text       TEXT"
            + ")");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public List<states> getAllProducts(){
        try (Statement statement = this.connection.createStatement()){
            List<states> states = new ArrayList<states>();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM states");
            
            while (resultSet.next()) {
                states.add(new states(resultSet.getInt("id"), resultSet.getString("EDUGroup"), (resultSet.getBoolean("weak")), 
                resultSet.getShort("day"), resultSet.getString("text")));
            }

            return states;
        }
        catch (SQLException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addState(states itemStates){
        try (PreparedStatement statement = this.connection.prepareStatement(
            "INSERT INTO states('EDUGroup', 'weak', 'day', 'text')" + "VALUES(?, ?, ?, ?)")){
            statement.setObject(1, itemStates.group);
            statement.setObject(2, itemStates.weak);
            statement.setObject(3, itemStates.day);
            statement.setObject(4, itemStates.text);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        if (this.connection != null){
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteState(int id){
        try {
            PreparedStatement statement = this.connection.prepareStatement(
            "DELETE FROM states WHERE id = ?"
            );
            statement.setObject(1, id);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
