package com.example;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class splitItem {

    private int []arr = new int[8];

    private String item;

    splitItem(String Item){
        item = Item;
    }

    public ArrayList<String> getSplitItem(){
        ArrayList<String> result = new ArrayList<String>();
        while(result.size() < 8) result.add("");

        // result.set(6, extra());
        result.set(0, getNum());
        result.set(2, getAUD());
        result.set(1, getSecName());
        result.set(3, getProf());
        result.set(4, getType());
        result.set(5, getTitle());
        result.set(6, result.get(6) + " " + item);
        result.set(7, getSubGroup());
        return result;
    }

    // 1, 2 или 0 если обе или не указано
    private String getSubGroup(){
        // item = "Системное программирование. Лабораторная работа, Камышанец А.К., ауд. 306 1 подгруппа: 1, 5, 9 и 13 неделя; 2 подгруппа: 3, 7, 11, и 15 неделя";
        Pattern pattern = Pattern.compile("\\s*\\d\\s?([пП]одгруп+[аАыЫ])");
        Matcher matcher = pattern.matcher(item);

        String subGroup = "0";
        while (matcher.find()){
            String tmp = matcher.group();
            tmp = tmp.stripLeading();
            if (subGroup.equals("0")){
                subGroup = tmp.substring(0, 1);
            }
            else{
                subGroup = "0";
            }
        }
        
        return subGroup;
    }

    // подгруппы и недели на будущее
    private String extra(){
        Pattern pattern = Pattern.compile("(((\\d[-]?\\s*подгруп*[аАыЫ]?)|([нН]едел[иИяЯьЬ]))[:-]?\\s?)+((\\d+[,])+\\d*)?");
        Matcher matcher = pattern.matcher(item);

        String tmp = "";
        ArrayDeque<Integer> st = new ArrayDeque<Integer>();
        ArrayDeque<Integer> end = new ArrayDeque<Integer>();

        while (matcher.find()){
            if (!tmp.equals("")){
                tmp += " $ ";
            }
            tmp += matcher.group();
            st.push(matcher.start());
            end.push(matcher.end());
        }
        while(st.size() != 0) {
            item = item.substring(0, st.poll()) + item.substring(end.poll());
        }

        return tmp;
    }

    // номер пары
    private String getNum(){
        String tmp = item.substring(0, 1);
        item = item.substring(1);
        item = item.stripTrailing();
        return tmp;
    }

    // фамилия инициалы
    private String getSecName(){
        Pattern pattern = Pattern.compile("([А-Я]\\S+\\s*[-]\\s*)?((?<=\\S\\s)[А-Я]{1,3}[а-яё]+)(\\s+.\\s?([, .]|\\s)((\\s\\S.\\s)|((?<=[,.])(\\S|\\s)\\s?[,.]?(\\S\\s)?)))");
        Matcher matcher = pattern.matcher(item);

        String tmp = "";
        while (matcher.find()){
            arr[0] = matcher.start();
            arr[1] = matcher.end();
            item = item.substring(0, matcher.start()) + item.substring(matcher.end());
            tmp += matcher.group();
        }

        // если нет то последнее слово с большой буквы
        if (tmp.equals("")){
            pattern = Pattern.compile("(?<=\\s)[А-Я][а-яё]+");
            matcher = pattern.matcher(item);
            while (matcher.find()){
                if (matcher.group().equals("Военная")){
                    continue;
                }
                arr[0] = matcher.start();
                arr[1] = matcher.end();
                tmp = matcher.group();
            }
            if (tmp != "")
                item = item.substring(0, arr[0]) + item.substring(arr[1]);
        }
        return tmp;
    }


    // аудитория
    private String getAUD(){
        Pattern pattern = Pattern.compile("(([аА]уд\\s*[,.]?\\s*\\S*)((?<=[аА]ктовый)(\\s*\\S*))?)|((\\S*\\s+|(\\S*(?=\\s?)))[зЗ]ал)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item);

        if (matcher.find()){
            arr[2] = matcher.start();
            arr[3] = matcher.end();
            item = item.substring(0, matcher.start()) + item.substring(matcher.end());
            return matcher.group();
        }
        else{

            // последние цифры
            String tmp = "";
            pattern = Pattern.compile("(\\S[-])?\\d+\\S+");
            matcher = pattern.matcher(item);
            while (matcher.find()){
                arr[2] = matcher.start();
                arr[3] = matcher.end();
                tmp = matcher.group();
            }
            if (tmp != "")
                item = item.substring(0, arr[2]) + item.substring(arr[3]);
            return tmp;
        }
    }

    // академические звания
    private String getProf(){
        Pattern pattern = Pattern.compile("(профессор)|(доцент)|(старший\\s*преподаватель)|(ассистент)|([нН]ачальник\\s+[оО]тдела\\s+([оО][пП])\\s+[иИ]\\s+([уУ][чЧ][рР]))", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()){
            arr[4] = matcher.start();
            arr[5] = matcher.end();
            item = item.substring(0, matcher.start()) + item.substring(matcher.end());
            return matcher.group();
        }
        else{
            return "";
        }
    }

    // тип пары
    private String getType(){
        String regex = "((\\s+([лЛ]\\S{1,4}[цЦ][иИ]*\\S{1,2}[яЯиИ])|([пП]\\S{1,4}[кК]\\S{1,2}[иИ]\\S{1,2}[аА]))+)(?:\\S*\\s*((([пП]\\S{1,4}[кК]\\S{1,2}[иИ]\\S{1,2}[аА])|([лЛ]\\S{1,4}[цЦ][иИ]*\\S{1,2}[яЯ]))+))?|(([лЛ]\\S+[рР]\\S+[рР]\\S+[аАыЫ][еЕяЯ]\\s*[рР]\\S+[бБ]\\S+[аАыЫ]))";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()){
            arr[6] = matcher.start();
            arr[7] = matcher.end();
            item = item.substring(0, matcher.start()) + item.substring(matcher.end());
            return matcher.group();
        }
        else{
            return "";
        }
    }

    // название предмета (с начала до первой регулярки)
    private String getTitle(){
        int min = getMin();
        String tmp = item.substring(0, min);
        item = item.substring(min);
        return tmp;
    }

    // первая регулярка для названия
    private int getMin(){
        int min = 1000;
        for (int i : arr) {
            if (i < min && i != 0){
                min = i;
            }
        }
        if (min == 1000){
            min = 0;
        }
        return min;
    }

    private int getMax(){
        int max = 0;
        for (int i : arr) {
            if (i > max){
                max = i;
            }
        }
        return max;
    }
}