package com.example;

import java.sql.SQLException;
import java.util.ArrayList;

public class fillDBWithThis {
    ReadExcel RE;
    dbHandler mDbHandler;

    fillDBWithThis(String fileName, String nameForTabCourse){
        RE = new ReadExcel(fileName);

        try {
            mDbHandler = dbHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        parseAll(nameForTabCourse);

        RE.close();
    }

    private void parseAll(String nameForTabCourse){
        short csNum = 0, grNum = 0;

        mDbHandler.createTabForCourse(nameForTabCourse);

        for (String cs : RE.getSheetsNames()){
            RE.setSheetIndex(csNum);
            if (mDbHandler.checkCourseIfExist(cs, nameForTabCourse) == false){
                mDbHandler.addStateForCourse(new states(cs), nameForTabCourse);
            }
            for (String gr : RE.getGroupNames()) {
                RE.setGroup(grNum);

                mDbHandler.createGroupTab(gr);

                // int code = mDbHandler.getCourseCode(cs, nameForTabCourse);
                // String tabForGroup = nameForTabCourse + "k" + code;

                // mDbHandler.createTabforGroup(tabForGroup);
                // insertWeeks(cs, gr, tabForGroup);
                insertWeeks(cs, gr, gr);

                grNum += 1;
            }
            grNum = 0;
            csNum += 1;
        }
    }

    private void insertWeeks(String course, String group, String nameForTabGroup){
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
                mDbHandler.addStateInGroup(new states(weak, day, item), nameForTabGroup);
                // mDbHandler.addStateforGroup(new states(course, group, weak, day, item), nameForTabGroup);
            }
            day += 1;
        }
    }
}
