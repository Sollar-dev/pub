package com.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        String FILE_NAME = "xlsx/etf_vesna24.xlsx";

        new fillWithThis(FILE_NAME);

        try {
            dbHandler mDbHandler = dbHandler.getInstance("gr");

            List<states> mStates = mDbHandler.getAllProducts();
            for (states itemStates : mStates){
                System.out.println(itemStates.toString());
            }

            mDbHandler.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // File forDel = new File("db/test.db.txt");
        // forDel.delete();
    }
}