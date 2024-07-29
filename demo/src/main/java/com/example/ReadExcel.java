package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

// установка файла через конструктор
// получение имен страниц
// вызов функции для установки выбранной страницы
// получение названий групп
// установка группы
public class ReadExcel {
    private InputStream ReadFile;   // поток чтения файла
    private XSSFWorkbook workbook;  // книга
    private int sheetIndex;         // номер выбранной страницы
    private int even, odd;          // номера строк для четной, нечетной недель
    private int group;              // номер выбранной группы

    private ArrayList<String> sheetsNames = new ArrayList<String>();  // имена листов
    private ArrayList<String> groupNames = new ArrayList<String>();   // названия групп
    private ArrayList<Integer> groupIndex = new ArrayList<Integer>(); // номера столбцов групп
    private int sheetsAmount;       // количество листов
    private int groupAmount;        // количество групп

    ReadExcel() {
    }

    ReadExcel(String fileName) {
        try {
            // ReadFile = getClass().getResourceAsStream(fileName);
            ReadFile = new FileInputStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReadFile = openFileRead(fileName);

        workbook = null;
        try {
            workbook = new XSSFWorkbook(ReadFile);
        } catch (Exception e) {
            e.printStackTrace();
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
    public void setSheetIndex(int sheetindex) {
        sheetIndex = sheetindex;
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        setRows(sheet);
        setGroupNames(sheet);
    }

    // получение имен листов, их количества
    private void setSheetNames() {
        sheetsAmount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetsAmount; i++) {
            sheetsNames.add(i, workbook.getSheetName(i));
        }
    }

    // открытие потока чтения файла
    private InputStream openFileRead(String fileName) {
        InputStream read = null;
        try {
            read = new FileInputStream(fileName);
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
        groupNames.clear();
        groupIndex.clear();
        groupAmount = 0;
        
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
        //int srcWeak = weak;
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
            ArrayList<String> tmp = dayItems(groupIndex.get(group), endX, days.get(i), days.get(i + 1));
            weakShedule.add(tmp);
        }

        return weakShedule;
    }

    // расписание на день
    private ArrayList<String> dayItems(int startX, int endX, int startY, int endY) {
        ArrayList<String> items = new ArrayList<String>();
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(startY);
        Cell cell = row.getCell(startX);
        boolean f = false;
        String item = "";
        char num = '1';
        int[] forNext = new int[] {0, 0, 0, 0, 0, 0};
        int[] used = new int[] {0, 0, 0, 0, 0, 0};

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
                    int indexNum = num - '1';
                    used[indexNum] += 1;
                    item += num;
                    item += " ";
                    item += cell.getStringCellValue();
                    row = sheet.getRow(y);
                    cell = row.getCell(x);
                    item += " ";
                    item += cell.getStringCellValue();

                    items.add(item);

                    if (downDoubleMerge(y, x)) {
                        forNext[indexNum + 1] += 1;
                    }
                }

                if (cell.getStringCellValue().equals("") && !f && (groupIndex.get(group) == x)){
                    Cell cellForWide;
                    int prevGroup = group - 1;
                    while(prevGroup >= 0){
                        cellForWide = row.getCell(groupIndex.get(prevGroup) + (x - groupIndex.get(group)));
                        if (row.getCell(groupIndex.get(prevGroup) + (x - groupIndex.get(group))) != null){
                            cellForWide = row.getCell(groupIndex.get(prevGroup) + (x - groupIndex.get(group)));
                        }
                        else {
                            cellForWide.setCellValue("");
                        }
                        if (!cellForWide.getStringCellValue().equals("")){
                            if (isWideMerged(row.getRowNum(), cellForWide.getColumnIndex()) == true){
                                    item = "";
                                    int indexNum = num - '1';
                                    used[indexNum] += 1;
                                    item += num;
                                    item += " ";
                                    item += cellForWide.getStringCellValue();
                                    row = sheet.getRow(y);
                                    item += " ";
                                    cellForWide = row.getCell(groupIndex.get(prevGroup) + (x - groupIndex.get(group)));
                                    item += cellForWide.getStringCellValue();
            
                                    items.add(item);
                                    break;
                            }
                        }
                        
                        prevGroup -= 1;
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
        return finalizeDayItems(items, used, forNext);
    }

    // private boolean forQuad(){

    // }

    // входит ли группа в широкий предмет
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

    // финальная редакция расписания на день
    private ArrayList<String> finalizeDayItems(ArrayList<String> items, int[] used, int[] forNext) {
        ArrayList<String> finalItems = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < 6; i++) {
            if (used[i] != 0) {
                while (used[i] != 0) {
                    finalItems.add(trimSpaces(items.get(count)));
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

    // удалает лишние пробелы
    private String trimSpaces(String item){
        String str = "";
        while(true){
            item = item.replaceFirst(" ", "*");
            if (!item.contains("*")){
                str += item;
                break;
            }
            str += item.substring(0, item.indexOf("*", 0)) + " ";
            item = item.substring(item.indexOf("*", 0) + 1, item.length());
            item = item.stripLeading();
        }

        return str;
    }

    // двойная пара? вниз
    private boolean downDoubleMerge(int row, int column) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int sheetMergeCount = sheet.getNumMergedRegions();
        CellRangeAddress ca;
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);
            if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
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