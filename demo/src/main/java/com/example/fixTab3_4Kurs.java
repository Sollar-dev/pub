package com.example;

import java.sql.SQLException;

public class fixTab3_4Kurs {
    dbHandler mDbHandler;

    fixTab3_4Kurs(){
        try {
            mDbHandler = dbHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbHandler.addStateGroupInCourse(new states("М-22", false), "bac 3 курс");
        mDbHandler.addStateGroupInCourse(new states("Н-22", false), "bac 3 курс");
        mDbHandler.addStateGroupInCourse(new states("Н-21", false), "bac 4 курс");

        mDbHandler.delUnnecessaryTab();
    }
}