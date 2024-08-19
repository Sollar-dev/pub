package com.example;

import java.sql.SQLException;
import java.util.ArrayList;

public class fillDBWithThis {
    ReadExcel RE;
    dbHandler mDbHandler;

    fillDBWithThis(String fileName, String edu){
        RE = new ReadExcel(fileName);

        try {
            mDbHandler = dbHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        parseAll(edu);

        RE.close();
    }

    private void parseAll(String edu){
        short csNum = 0, grNum = 0;

        mDbHandler.createTabForCourse(edu);

        for (String cs : RE.getSheetsNames()){
            RE.setSheetIndex(csNum);
            if (mDbHandler.checkCourseIfExist(cs, edu) == false){
                mDbHandler.addStateForCourse(new states(cs, true), edu);
                mDbHandler.createCourseTabforGroup(edu + " " + cs);
            }
            for (String gr : RE.getGroupNames()) {
                mDbHandler.addStateGroupInCourse(new states(gr, false), edu + " " + cs);
                RE.setGroup(grNum);

                mDbHandler.createGroupTab(gr);

                insertWeeks(gr);

                grNum += 1;
            }
            grNum = 0;
            csNum += 1;
        }
    }

    private void insertWeeks(String nameForTabGroup){
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
                splitItem sItem = new splitItem(item);
                ArrayList<String> parts = sItem.getSplitItem();
                if (parts.get(6) != ""){
                    System.out.println(nameForTabGroup + " " + parts.get(6));
                }
                mDbHandler.addStateInGroup(new states(weak, day, 
                parts.get(0), parts.get(1), parts.get(2), parts.get(3), parts.get(4), parts.get(5), parts.get(6))
                , nameForTabGroup);
            }
            day += 1;
        }
    }
}
