package com.example;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class dbHandler {
    private static final String CON_STR = "jdbc:sqlite:db/test.db";

    private static dbHandler instance = null;
    private static String tableName = "";

    public static synchronized dbHandler getInstance(String TableName) throws SQLException{
        tableName = TableName;
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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " ("
            + " id         INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " course     TEXT NOT NULL,"
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + "");
            
            int a = 0;
            while (resultSet.next()) {
                states.add(new states(resultSet.getInt("id"), resultSet.getString("course"), resultSet.getString("EDUGroup"), (resultSet.getBoolean("weak")), 
                resultSet.getShort("day"), resultSet.getString("text")));
                if (a == 10){
                    return states;
                }
                a += 1;
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
            "INSERT INTO " +tableName + "('course', 'EDUGroup', 'weak', 'day', 'text')" + "VALUES(?, ?, ?, ?, ?)")){
            statement.setObject(1, itemStates.course);
            statement.setObject(2, itemStates.group);
            statement.setObject(3, itemStates.weak);
            statement.setObject(4, itemStates.day);
            statement.setObject(5, itemStates.text);

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
            "DELETE FROM " + tableName + " WHERE id = ?"
            );
            statement.setObject(1, id);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
