package com.example;

import org.sqlite.JDBC;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class dbHandler {
    private static final String CON_STR = "jdbc:sqlite:db/full.db";
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
    }

    // образование + курс, добавляем группы
    public void createCourseTabforGroup(String tableName){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS [" + tableName + "] ("
            + " EDUGroup   TEXT NOT NULL,"
            + " id         INTEGER PRIMARY KEY AUTOINCREMENT"
            + ")");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    // образование, добавляем курсы
    public void createTabForCourse(String tableName){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " ("
            + " courseName      TEXT NOT NULL,"
            + " courseCode      INTEGER PRIMARY KEY AUTOINCREMENT"
            + ")");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    // группы, добавляем расписание
    public void createGroupTab(String tableName){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS [" + tableName + "] ("
            + " id          INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " weak        INTEGER,"
            + " day         INTEGER NOT NULL,"
            + " num         TEXT,"
            + " title       TEXT,"
            + " type        TEXT,"
            + " aud         TEXT,"
            + " prof        TEXT,"
            + " secName     TEXT,"
            + " extra       TEXT,"
            + " subGroup    TEXT"
            + ")");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    // предмет в группу
    public void addStateInGroup(states itemStates, String tableName){
        try (PreparedStatement statement = this.connection.prepareStatement(
            "INSERT INTO [" + tableName + "]('weak', 'day', 'num', 'title', 'type', 'aud', 'prof', 'secName', 'extra', 'subGroup')" + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
            statement.setObject(1, itemStates.weak);
            statement.setObject(2, itemStates.day);
            statement.setObject(3, itemStates.num);
            statement.setObject(4, itemStates.title);
            statement.setObject(5, itemStates.type);
            statement.setObject(6, itemStates.aud);
            statement.setObject(7, itemStates.prof);
            statement.setObject(8, itemStates.secName);
            statement.setObject(9, itemStates.extra);
            statement.setObject(10, itemStates.subGroup);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // курс в образование
    public void addStateForCourse(states itemStates, String tableName){
        try (PreparedStatement statement = this.connection.prepareStatement(
            "INSERT INTO " + tableName + "('courseName')" + "VALUES(?)")){
            statement.setObject(1, itemStates.courseName);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // группу в курс
    public void addStateGroupInCourse(states itemStates, String tableName){
        try (PreparedStatement statement = this.connection.prepareStatement(
            "INSERT INTO [" + tableName + "]('EDUGroup')" + "VALUES(?)")){
            statement.setObject(1, itemStates.group);

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

    public void deleteState(int id, String tableName){
        try {
            PreparedStatement statement = this.connection.prepareStatement(
            "DELETE FROM [" + tableName + "] WHERE id = ?"
            );
            statement.setObject(1, id);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // существует ли курс
    public boolean checkCourseIfExist(String course, String tableName){
        try (Statement statement = this.connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE courseName='" + course + "'");

            int code = resultSet.getInt("courseCode");
            if (code == 0){
                return false;
            }
            else{
                return true;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}