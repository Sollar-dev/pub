package com.example;

import java.sql.SQLException;
import java.util.ArrayList;

public class fillWithThis {
    // InputStream inputStream = null;
    ReadExcel RE;
    dbHandler mDbHandler;

    fillWithThis(String fileName){
        RE = new ReadExcel(fileName);

        parseAll();

        RE.close();
        //mDbHandler.closeConnection();
    }

    private void parseAll(){
        short csNum = 0, grNum = 0;

        for (String cs : RE.getSheetsNames()){
            RE.setSheetIndex(csNum);
            for (String gr : RE.getGroupNames()) {
                try {
                    mDbHandler = dbHandler.getInstance("gr");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                RE.setGroup(grNum);

                insertWeek(cs, gr);

                grNum += 1;
            }
            grNum = 0;
            csNum += 1;
        }
    }

    private void insertWeek(String course, String group){
        // 0 четная, 1 нечетная
        ArrayList<ArrayList<String>> weekItems = RE.DoubleWeak();

        short day = 0;
        boolean weak = true;
        for (ArrayList<String> dayItems : weekItems){
            if (day == 6){
                day = 0;
                weak = false;
            }
            for (String item : dayItems){
                mDbHandler.addState(new states(course, group, weak, day, item));
            }
            day += 1;
        }
    }
}
