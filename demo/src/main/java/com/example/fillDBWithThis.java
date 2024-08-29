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

    // проход всего файла
    private void parseAll(String edu){
        short csNum = 0, grNum = 0;

        mDbHandler.createTabForCourse(edu);

        for (String cs : RE.getSheetsNames()){
            RE.setSheetIndex(csNum);
            if (mDbHandler.checkCourseIfExist(cs, edu) == false){

                // таблица образования с курсами
                mDbHandler.addStateForCourse(new states(cs, true), edu);

                // таблица курсов (для каждого образования разные) с группами
                mDbHandler.createCourseTabforGroup(edu + " " + cs);
            }
            for (String gr : RE.getGroupNames()) {

                // в курс добавляем группу
                mDbHandler.addStateGroupInCourse(new states(gr.strip(), false), edu + " " + cs);
                RE.setGroup(grNum);

                // таблица группы
                mDbHandler.createGroupTab(gr.strip());

                insertWeeks(gr.strip());

                grNum += 1;
            }
            grNum = 0;
            csNum += 1;
        }
    }

    // вставляем в таблицу групп недели расписания
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

                // неделя, день, номер пары, фамилия, аудитория, академическое звание, тип, название, дополнительная информация, подгруппа
                mDbHandler.addStateInGroup(new states(weak, day, 
                parts.get(0), parts.get(1), parts.get(2), parts.get(3), parts.get(4), parts.get(5), parts.get(6), parts.get(7))
                , nameForTabGroup);
            }
            day += 1;
        }
    }
}
