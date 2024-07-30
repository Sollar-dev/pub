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

        // строка для поиска
        String reference = "Дни недели";    

        int rowIndex = 0;
        Row row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(0);
        String current = cell.getStringCellValue();

        // ищем 2 недели
        while (count != 2) {
            if (current.equals(reference)) {

                // сначало нечетная, потом четная
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

                // сравниваемая строка
                current = cell.getStringCellValue(); 
            }
        }
    }

    // получение имен групп и их количества
    private void setGroupNames(Sheet sheet) {

        // очистка с предыдущих итераций
        groupNames.clear();
        groupIndex.clear();
        groupAmount = 0;
        
        Row row = sheet.getRow(odd);

        // столбец с именами групп
        int count = timeCell() + 1;
        Cell cell = row.getCell(count);

        // максимум столбцов
        int max = row.getPhysicalNumberOfCells();

        while (count < max) {
            // идем до конца включаем все не пустые и кроме избранной
            if (!cell.getStringCellValue().equals("") && !cell.getStringCellValue().equals("№ пары")) {
                groupNames.add(cell.getStringCellValue());

                // добавляем номер столбца начала группы
                groupIndex.add(count);
            }
            count += 1;
            cell = row.getCell(count);
        }

        // количество групп
        groupAmount = groupNames.size();
    }

    // двойная неделя
    public ArrayList<ArrayList<String>> DoubleWeak(){
        ArrayList<ArrayList<String>> weakShedule = new ArrayList<ArrayList<String>>();

        // четная
        weakShedule = WeakShedule(0);

        // нечетная
        weakShedule.addAll(WeakShedule(1));

        return weakShedule;
    }

    // расписание на неделю
    public ArrayList<ArrayList<String>> WeakShedule(int weak) {
        if (weak == 0) {
            // четная
            weak = even;
        } else {
            // нечетная
            weak = odd;
        }

        // номера столбцов начала дней и конца расписания
        ArrayList<Integer> days = daysIndex(weak);
        ArrayList<ArrayList<String>> weakShedule = new ArrayList<ArrayList<String>>();

        // конец следующая группа или конец файла
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

        // чередует сроки
        boolean f = false;

        // для добавления
        String item = "";

        // номер пары
        char num = '1';

        //пометка что пара двойная
        int[] forNext = new int[] {0, 0, 0, 0, 0, 0};

        //количество предметов в одном номере пары
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

                    // номер пары для массива
                    int indexNum = num - '1';
                    // двойная особая пара
                    if (forQuad(cell.getStringCellValue())){

                        // предыдущий предмет как начало строки
                        item = items.get(items.size() - 1);
                        items.remove(items.size() - 1);

                        // двойная
                        forNext[indexNum] += 1;
                    }
                    else{
                        used[indexNum] += 1;
                        item += num;

                        //двойная пара обычная (объедененная клетка)
                        if (downDoubleMerge(y, x)) {
                            forNext[indexNum + 1] += 1;
                        }
                    }

                    item += " ";
                    item += cell.getStringCellValue();

                    row = sheet.getRow(y);
                    cell = row.getCell(x);
                    item += " ";
                    item += cell.getStringCellValue();

                    items.add(item);
                }

                // для широкой пары
                if (cell.getStringCellValue().equals("") && !f && (groupIndex.get(group) == x)){
                    Cell cellForWide;
                    int prevGroup = group - 1;

                    // проверка Объединенных ячеек в предыдущий группах
                    while(prevGroup >= 0){
                        int cellIndex = groupIndex.get(prevGroup) + (x - groupIndex.get(group));
                        cellForWide = row.getCell(cellIndex);
                        if (row.getCell(cellIndex) != null){
                            cellForWide = row.getCell(cellIndex);
                        }
                        else {
                            cellForWide.setCellValue("");
                        }

                        // проходят ли их границы через текущую группу
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

            // чередование строк
            if (f) {
                num += 1;
                f = false;
            } else {
                f = true;
            }
        }
        return finalizeDayItems(items, used, forNext);
    }

    private ArrayList<String> timeIndexes(int start, int end){
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(start);
        ArrayList<String> items = new ArrayList<String>();
        int baseX = timeCell();
        Cell cell = row.getCell(baseX);

        for (int i = start; i < end; i++){

            

            row = sheet.getRow(i);
            if (row.getCell(baseX) != null){
                cell = row.getCell(baseX);
            }
            else{
                cell.setCellValue("");
            }
        }

        return items;
    }

    // для проверки особых двойных пар
    private boolean forQuad(String src){
        boolean f = false;

        // обрезаем пробелы
        src = src.stripLeading();
        src = src.stripTrailing();    

        ArrayList<String> ref = new ArrayList<>();
        ref.add("лабораторные работы");
        ref.add("ассистент Александрова А.А.");
        ref.add("старший преподаватель Елистратов С.С.");
        ref.add("практика");
        ref.add("недели: 24,28,32,36  1 подгруппа Спортивный зал");
        ref.add("Панов А.Ю.");
        ref.add("профессор Логунов В.И.");
        ref.add("лекции");
        ref.add("доцент Шумилов Е.А.");
        ref.add("Кураторский час");
        ref.add("Гурина Я.В.");
        ref.add("лекция");
        ref.add("Любавина О.С.");
        ref.add("доцент Поднебесова М.И.");
        ref.add("доцент Осипов А.П.");
        ref.add("доцент Савельев В.В.");
        ref.add("доцент  Широков А.В.");
        ref.add("практика  ");
        ref.add("доцент Денисова О.Н.");
        ref.add("доцент    Чичкина В.Д.");
        ref.add("старший преподаватель Буркина Т.А.");
        ref.add("доцент Литвинов В.Л.");
        ref.add("недели: 27,31");
        ref.add("ассистент Васецкая Е.С.");
        ref.add("ЭКЗАМЕН");
        ref.add("ЗАЧЁТ с ОЦЕНКОЙ");
        ref.add("лекция недели:27,31,35,39");
        // ref.add("");
        // ref.add("");

        // сравнение
        for (int i = 0; i < ref.size(); i++){
            if (src.equals(ref.get(i))){
                f = true;
            }
        }
        return f;
    }

    // входит ли группа в широкий предмет
    private boolean isWideMerged(int row, int column) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);

        // количество объединенных ячеек
        int sheetMergeCount = sheet.getNumMergedRegions();

        // углы ячейки
        CellRangeAddress ca;

        // проверяем все 
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);

            // совпал один угол
            if (row == ca.getFirstRow() && column == ca.getFirstColumn()) {

                // если правое ребро проходит через наш столбец
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

                // добавляем пару
                while (used[i] != 0) {
                    // удаляем лишние пробелы
                    finalItems.add(trimSpaces(items.get(count)));
                    count++;
                    used[i] -= 1;
                }
            } else {

                // если двойная особые символ на следующую пару
                if (forNext[i] != 0){
                    finalItems.add("&");
                }
                else {

                    // если пустая - только номер пары
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

        // количество объединенных ячеек
        int sheetMergeCount = sheet.getNumMergedRegions();

        // углы ячейки
        CellRangeAddress ca;
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);

            if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {

                // если нижнее ребро дальше чем верхнее более чем на 1 строку
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

            // для нечетной конец расписания начало четной недели
            last = even;
        } else {
            last = endSheduleVertical();
        }
        weak += 1;
        Row row = sheet.getRow(weak);

        // дни недели - первый столбец
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

        // по столбцу времени 
        int time = timeCell();
        int miss = 0, last = even, count = even + 1;
        cell = row.getCell(time);

        // 5 пустых подряд - конец расписания
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
    private int timeCell() {
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