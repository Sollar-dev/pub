package com.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ArrayList<String> items = new ArrayList<String>();

        ArrayList<ArrayList<String>> weak;
        String FILE_NAME = "/etf_vesna24.xlsx";
        try {
            ReadExcel RE = new ReadExcel(FILE_NAME);
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