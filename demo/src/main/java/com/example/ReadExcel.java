package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.net.URL;

// установка файла через конструктор
// получение имен страниц
// вызов функции для установки выбранной страницы
// получение названий групп
// установка группы
public class ReadExcel {
    private String FileName;        // имя файла excel
    private InputStream ReadFile;   // поток чтения файла
    private Workbook workbook;      // книга
    private int sheetIndex;         // номер выбранной страницы
    private int even, odd;          // номера строк для четной, нечетной недель
    private int group;              // номер выбранной группы

    private ArrayList<String> sheetsNames = new ArrayList<String>(); // имена листов
    private ArrayList<String> groupNames = new ArrayList<String>();  // названия групп
    private ArrayList<Integer> groupIndex = new ArrayList<Integer>(); // номера столбцов групп
    private int sheetsAmount;        // количество листов
    private int groupAmount;         // количество групп

    ReadExcel() {
    }

    // Не главный конструктор
    ReadExcel(String fileName) {
        FileName = fileName;
        try {
            //URL url = this.getClass().getResource(fileName);
            ReadFile = getClass().getResourceAsStream(fileName);//url.openStream();
            //ReadFile = new FileInputStream(FileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        workbook = null;
        try {
            workbook = WorkbookFactory.create(ReadFile);
        } catch (Exception e) {
            System.out.println("Read error");
        }

        setSheetNames();
    }

    // тестовый конструктор
    ReadExcel(String fileName, int sheetindex) {
        FileName = fileName;
        ReadFile = openFileRead();
        setSheetIndex(sheetindex);
        Main();
    }

    // главный конструктор
    ReadExcel(InputStream fis) {
        ReadFile = fis;

        workbook = null;
        try {
            workbook = WorkbookFactory.create(ReadFile);
        } catch (Exception e) {
            System.out.println("Read error");
        }

        setSheetNames();
    }

    // установка группы
    public void setGroup(int gr) {
        group = gr;
    }

    // отправление количества листов
    public int getSheetsAmount() {
        return sheetsAmount;
    }

    // отправление количества групп
    public int getGroupAmount() {
        return groupAmount;
    }

     // отправление имен листов
    public ArrayList<String> getSheetsNames() {
        return sheetsNames;
    }

    // отправление названий групп
    public ArrayList<String> getGroupNames() {
        return groupNames;
    }

    // установка выбранной страницы
    // взов главного метода
    public void setSheetIndex(int sheetindex) {
        sheetIndex = sheetindex;
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        setRows(sheet);
        setGroupNames(sheet);
//        mergedCellList();
    }

    // получение имен листов, их количества
    private void setSheetNames() {
        sheetsAmount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetsAmount; i++) {
            sheetsNames.add(i, workbook.getSheetName(i));
        }
    }

    // открытие потока чтения файла
    private InputStream openFileRead() {
        InputStream read = null;
        try {
            read = new FileInputStream(FileName);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }

        return read;
    }

    // установка номеров строк с неделями
    private void setRows(Sheet sheet) {
        int count = 0;
        String reference = "Дни недели";    // строка для сравнения

        int rowIndex = 0;
        Row row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(0);

        String current = cell.getStringCellValue();
        while (count != 2) {
            if (current.equals(reference)) {
                if (count == 0) {
                    odd = rowIndex;
                    count += 1;
                } else {
                    even = rowIndex;
                    count += 1;
                }
            }
            rowIndex += 1;
            row = sheet.getRow(rowIndex);
            cell = row.getCell(0);
            if (cell != null){
                current = cell.getStringCellValue(); // сравниваемая строка
            }
        }
    }

    // получение имен групп и их количества
    private void setGroupNames(Sheet sheet) {
        Row row = sheet.getRow(odd);
        int count = Time() + 1;
        Cell cell = row.getCell(count);
        int max = row.getPhysicalNumberOfCells();
        while (count < max) { // не актуал (до 30 столбца)
            if (!cell.getStringCellValue().equals("") && !cell.getStringCellValue().equals("№ пары")) {
                groupNames.add(cell.getStringCellValue());
                groupIndex.add(count);
            }
            count += 1;
            cell = row.getCell(count);
        }
        groupAmount = groupNames.size();
    }

    // двойная неделя
    public ArrayList<ArrayList<String>> DoubleWeak(){
        ArrayList<ArrayList<String>> weakShedule = new ArrayList<ArrayList<String>>();
        weakShedule = WeakShedule(0);
        weakShedule.addAll(WeakShedule(1));
        return weakShedule;
    }

    // расписание на неделю
    public ArrayList<ArrayList<String>> WeakShedule(int weak) {
        int srcWeak = weak;
        if (weak == 0) {
            weak = even;
        } else {
            weak = odd;
        }
        ArrayList<Integer> days = daysIndex(weak);
        ArrayList<ArrayList<String>> weakShedule = new ArrayList<ArrayList<String>>();
        int endX;
        if (groupIndex.get(group) == groupIndex.get(groupIndex.size() - 1)) {
            endX = groupIndex.get(group) + 6;
        } else {
            endX = groupIndex.get(group + 1);
        }
        for (int i = 0; i < days.size() - 1; i += 1) {
            // ArrayList<String> test = new ArrayList<String>(0);
            // test.add(0, "te");
            ArrayList<String> tmp = dayItems(groupIndex.get(group), endX, days.get(i), days.get(i + 1));
            //fillWideItems(tmp, srcWeak, i);
            weakShedule.add(tmp);
        }

        return weakShedule;
    }

//    private ArrayList<String> fillWideItems(ArrayList<String> dayItems, int weak, int day){
//        Sheet sheet = workbook.getSheetAt(sheetIndex);
//
//        for (int i = 0; i < wideItemsInfo.size(); i++){
//            if (wideItemsInfo.get(i).get(0) == group && wideItemsInfo.get(i).get(1) == weak && wideItemsInfo.get(i).get(2) == day){
//                Row row = sheet.getRow(wideItemsInfo.get(i).get(4));
//                Cell cell = row.getCell(groupIndex.get(group - 1));
//                dayItems.add(wideItemsInfo.get(i).get(3), wideItems.get(i));
//            }
//        }
//        return dayItems;
//    }

    // расписание на день
    private ArrayList<String> dayItems(int startX, int endX, int startY, int endY) {
        ArrayList<String> items = new ArrayList<String>();
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(startY);
        Cell cell = row.getCell(startX);
        boolean f = false, wide = false;
        boolean LR = false;
        String item = "";
        char num = '1';
        int[] forNext = new int[] {0, 0, 0, 0, 0, 0};
        int[] used = new int[]{0, 0, 0, 0, 0, 0};
        for (int y = startY + 1; y < endY; y += 1) {
            for (int x = startX; x < endX; x += 1) {
                if (row.getCell(x) != null){
                    cell = row.getCell(x);
                }
                else {
                    cell.setCellValue("");
                }
                if (!cell.getStringCellValue().equals("") && !f) {
                    item = "";
                    int intNum = num - '1';
                    used[intNum] += 1;
                    if (compForQuad(cell.getStringCellValue()) == true){
                        LR = true;
                        used[intNum] -= 1;
                        forNext[intNum] += 1;
                    }
                    else {
                        item += num;
                        item += " ";
                    }
                    item += cell.getStringCellValue();
                    row = sheet.getRow(y);
                    cell = row.getCell(x);
                    item += " ";
                    item += cell.getStringCellValue();
                    if (LR == true){
                        String prev = items.get(items.size() - 1);
                        items.remove(items.size() - 1);
                        prev += " " + item;
                        items.add(prev);
                        LR = false;
                    }
                    else {
                        items.add(item);
                    }
                    if (isMergedRegion(y, x)) {
                        forNext[intNum + 1] += 1;
                    }
                }
                if (cell.getStringCellValue().equals("") && groupIndex.get(group) != groupIndex.get(0)){
                    if (isWideMerged(y, (x - (groupIndex.get(group) - groupIndex.get(group - 1)))) == true) {
                        item = "";
                        item += num;
                        item += " ";
                        int intNum = num - '1';
                        cell = row.getCell(groupIndex.get(group - 1));
                        item += cell.getStringCellValue();
                        row = sheet.getRow(y);
                        cell = row.getCell(groupIndex.get(group - 1));
                        item += " ";
                        item += cell.getStringCellValue();
                        used[intNum] += 1;
                        items.add(item);
//                        if (isMergedRegion(y, x)) {
//                            forNext[intNum + 1] += 1;
//                        }
                    }
                }
                row = sheet.getRow(y - 1);
            }
            row = sheet.getRow(y);
            if (f) {
                num += 1;
                f = false;
            } else {
                f = true;
            }
        }
//        return items;
        return finalizeDayItems(items, used, forNext);
    }

    private boolean compForQuad(String src){
        boolean f = false;
        ArrayList<String> ref = new ArrayList<>();
        ref.add("лабораторные работы");
        ref.add("ассистент Александрова А.А.");
        ref.add("старший преподаватель Елистратов С.С.");
        ref.add("практика");
        ref.add(" недели: 24,28,32,36  1 подгруппа Спортивный зал");
        ref.add("Панов А.Ю.");
        ref.add("профессор Логунов В.И.");
        ref.add("");
        ref.add("");
        ref.add("");
        for (int i = 0; i < ref.size(); i++){
            if (src.equals(ref.get(i))){
                f = true;
            }
        }
        return f;
    }

    private ArrayList<String> finalizeDayItems(ArrayList<String> items, int[] used, int[] forNext) {
        ArrayList<String> finalItems = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < 6; i++) {
            if (used[i] != 0) {
                while (used[i] != 0) {
                    finalItems.add(items.get(count));
                    count++;
                    used[i] -= 1;
                }
            } else {
                if (forNext[i] != 0){
                    finalItems.add("&");
                }
                else {
                    String tmp = "";
                    tmp += i + 1;
                    finalItems.add(tmp);
                }
            }
        }
        return finalItems;
    }

//    private void mergedCellList(){
//        Sheet sheet = workbook.getSheetAt(sheetIndex);
//        int sheetMergeCount = sheet.getNumMergedRegions();
//        for (int i = 0; i < sheetMergeCount; i++) {
//            CellRangeAddress region = sheet.getMergedRegion(i);
//            mergedRegions.put(Pair.create(region.getFirstRow(), region.getFirstColumn()), 1);
//        }
//    }

//    Map<Pair<Integer, Integer>, Integer> mergedRegions = new HashMap<Pair<Integer, Integer>, Integer>();

//    private ArrayList<ArrayList<Integer>> wideItemsInfo = new ArrayList<ArrayList<Integer>>(); // // запись о широкой паре
//    private ArrayList<String> wideItems = new ArrayList<String>(); // широкая пара

    private boolean isWideMerged(int row, int column) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int sheetMergeCount = sheet.getNumMergedRegions();
        CellRangeAddress ca;
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);
            if (row == ca.getFirstRow() && column == ca.getFirstColumn()) {
                if (ca.getLastColumn() >= groupIndex.get(group)){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    // двойная пара?
    private boolean isMergedRegion(int row, int column) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
//        Row row1 = sheet.getRow(row);
//        Cell cell = row1.getCell(column);
//        String t = cell.getStringCellValue();
        int sheetMergeCount = sheet.getNumMergedRegions();
        CellRangeAddress ca;
//        Integer b = mergedRegions.get(Pair.create(column, row));
//        if (b != null){
//            return true;
//        }
//        else {
//            return false;
//        }
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);
            if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
//                if (ca.getLastColumn() >= groupIndex.get(group + 1)){
//                    ArrayList<Integer> tmp = new ArrayList<>();
//                    tmp.add(group + 1);
//                    tmp.add(weak);
//                    tmp.add(day);
//                    tmp.add(numItem);
//                    wideItems.add(item);
//                    wideItemsInfo.add(tmp);
//                }
                if (ca.getLastRow() > ca.getFirstRow() + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    // возврат номеров строк начала дней недели
    private ArrayList<Integer> daysIndex(int weak) {
        ArrayList<Integer> days = new ArrayList<Integer>();
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int last;
        if (weak == odd) {
            last = even;
        } else {
            //last = ReturnEnd();
            last = endSheduleVertical();
        }
        weak += 1;
        Row row = sheet.getRow(weak);
        Cell cell = row.getCell(0);
        for (int i = weak + 1; i < last; i += 1) {
            if (!cell.getStringCellValue().equals("")) {
                days.add(i - 1);
            }
            row = sheet.getRow(i);
            if (row.getCell(0) != null) {
                cell = row.getCell(0);
            }
        }
        days.add(last);
        return days;
    }

    // конец расписания
    // возврат номера строки конца четной недели
    private int endSheduleVertical() {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(even);
        Cell cell = row.getCell(0);
        int time = Time();
        int miss = 0, last = even, count = even + 1;
        cell = row.getCell(time);
        while (miss != 5) {
            if (!cell.getStringCellValue().equals("")) {
                miss = 0;
                last = count + 1;
            } else {
                miss += 1;
            }
            count += 1;
            row = sheet.getRow(count);
            if (row.getCell(time) != null) {
                cell = row.getCell(time);
            }
        }
        return last;
    }

    // ТЕСТ конец расписания
    private int ReturnEnd() {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(even);
        Cell cell = row.getCell(0);
        int time = Time();
        int miss = 0, last = even - 2, count = even + 1;
        cell = row.getCell(time);
        while (!cell.getStringCellValue().equals("Начальник учебного отдела                                                                     О.Н. Денисова")) {
            last++;
            count++;
            row = sheet.getRow(count);
            cell = row.getCell(time);
        }
        return last;
    }

    // возврат номера столбца 'Время подачи звонков'
    private int Time() {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(even);
        Cell cell = row.getCell(0);
        int time = 0;
        while (!cell.getStringCellValue().equals("Время подачи звонков")) {
            time += 1;
            cell = row.getCell(time);
        }
        return time;
    }

    // главная функция
    // открытие книги для чтение
    private void Main() {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        setRows(sheet);
        setGroupNames(sheet);
        // функции сюда
        //...
    }

    // закрытие
    public void close() {
        // закрытие всего
        try {
            workbook.close();
            ReadFile.close();
        } catch (IOException e) {
            System.out.println("Close error");
        }
    }
}

//    private void mergedCell(){
//        Sheet sheet = workbook.getSheetAt(sheetIndex);
//        int sheetMergeCount = sheet.getNumMergedRegions();
//        for (int i = 0; i < sheetMergeCount; i++){
//            CellRangeAddress range = sheet.getMergedRegion(i);
//            Row row = sheet.getRow(range.getFirstRow());
//            Cell cell = row.getCell(range.getFirstColumn());
//            String z = range.formatAsString();
//        }
//        return;
//    }
//}