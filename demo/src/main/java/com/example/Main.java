package com.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.sqlite.JDBC;

public class Main {
    public static void main(String[] args) {

        ArrayList<String> items = new ArrayList<String>();

        ArrayList<ArrayList<String>> weak;
        ReadExcel RE = new ReadExcel();
        String FILE_NAME = "/etf_vesna24.xlsx";
        //String FILE_NAME = "etf_vesna24.xlsx";
        //InputStream inputStream = null;
        try {
            //inputStream = Main.class.getResourceAsStream(FILE_NAME);
            RE = new ReadExcel(FILE_NAME);
            RE.setSheetIndex(0); 
            RE.setGroup(0);
            weak = RE.DoubleWeak();
            items = weak.get(0);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            dbHandler mDbHandler = dbHandler.getInstance();

            for (String item : items) {
                mDbHandler.addState(new states("EI-21", false, (short)1, item));
            }
            //mDbHandler.deleteState(2);

            List<states> mStates = mDbHandler.getAllProducts();
            for (states itemStates : mStates){
                System.out.println(itemStates.toString());
            }
            
            for (int id = 1; id <= items.size(); id++){
                mDbHandler.deleteState(id);
            }

            mDbHandler.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}