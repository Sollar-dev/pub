package com.example;

import java.io.File;
import java.sql.SQLException;

public class readAllfiles {
    public readAllfiles(){
        readDir();

        dbHandler mDbHandler = null;

        try {
            mDbHandler = dbHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbHandler.closeConnection();
    }

    // в папках по образованию
    private void readDir(){
        File dir = new File("xlsx/");
        for (String item : dir.list()){
            readFiles(dir + "/" + item);
        }
    }

    private void readFiles(String filePath){
        File dir = new File(filePath);
        for (File item : dir.listFiles()){
            String fileRelativePath = "xlsx/";
            fileRelativePath += dir.getName() + "/" + item.getName();

            // путь к файлу, образование (для таблиц)
            new fillDBWithThis(fileRelativePath, dir.getName());
        }
    }
}