package com.example;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class splitItem {
    private int secNameStart = 0;
    private int secNameEnd = 0;
    private int audStart = 0;
    private int audEnd = 0;
    private int profStart = 0;
    private int profEnd = 0;
    private int typeStart = 0;
    private int typeEnd = 0;

    private int []arr = new int[8];

    private String item;

    splitItem(String Item){
        item = Item;
        // item = "1 Кураторский час Начальник отдела ОП и УЧР Гурина Я.В. ауд.3.5";
    }

    public ArrayList<String> getSplitItem(){
        ArrayList<String> result = new ArrayList<String>();
        while(result.size() < 7) result.add("");

        // result.set(6, extra());
        result.set(0, getNum());
        result.set(2, getAUD());
        result.set(1, getSecName());
        result.set(3, getProf());
        result.set(4, getType());
        result.set(5, getTitle());
        result.set(6, result.get(6) + " " + item);
        return result;
    }

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

    private String getNum(){
        String tmp = item.substring(0, 1);
        item = item.substring(1);
        return tmp;
    }

    private String getSecName(){
        Pattern pattern = Pattern.compile("((?<=\\S\\s)[А-Я]{1,3}[а-яё]+)(\\s+.\\s?([, .]|\\s)((\\s\\S.\\s)|((?<=[,.])(\\S|\\s)\\s?[,.]?(\\S\\s)?)))");
        Matcher matcher = pattern.matcher(item);

        String tmp = "";
        while (matcher.find()){
            if (matcher.group().equals("России ")){
                matcher.group();
                continue;
            }
            secNameStart = matcher.start();
            arr[0] = secNameStart;
            secNameEnd = matcher.end();
            arr[1] = secNameEnd;
            item = item.substring(0, secNameStart) + item.substring(secNameEnd);
            tmp += matcher.group();
        }
        if (tmp.equals("")){
            pattern = Pattern.compile("(?<=\\s)[А-Я][а-яё]+");
            matcher = pattern.matcher(item);
            while (matcher.find()){
                if (matcher.group().equals("Военная")){
                    continue;
                }
                secNameStart = matcher.start();
                arr[0] = secNameStart;
                secNameEnd = matcher.end();
                arr[1] = secNameEnd;
                tmp = matcher.group();
            }
            item = item.substring(0, secNameStart) + item.substring(secNameEnd);
        }
        return tmp;
    }

    private String getAUD(){
        Pattern pattern = Pattern.compile("(([аА]уд\\s*[,.]?\\s*\\S*)((?<=[аА]ктовый)(\\s*\\S*))?)|((\\S*\\s+|(\\S*(?=\\s?)))[зЗ]ал)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item);

        if (matcher.find()){
            audStart = matcher.start();
            arr[2] = audStart;
            audEnd = matcher.end();
            arr[3] = audEnd;
            item = item.substring(0, audStart) + item.substring(audEnd);
            return matcher.group();
        }
        else{
            String tmp = "";
            pattern = Pattern.compile("(\\S[-])?\\d+\\S+");
            matcher = pattern.matcher(item);
            while (matcher.find()){
                audStart = matcher.start();
                arr[2] = audStart;
                audEnd = matcher.end();
                arr[3] = audEnd;
                tmp = matcher.group();
            }
            item = item.substring(0, audStart) + item.substring(audEnd);
            return tmp;
        }
    }

    private String getProf(){
        Pattern pattern = Pattern.compile("(профессор)|(доцент)|(старший\\s*преподаватель)|(ассистент)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()){
            profStart = matcher.start();
            arr[4] = profStart;
            profEnd = matcher.end();
            arr[5] = profEnd;
            item = item.substring(0, profStart) + item.substring(profEnd);
            return matcher.group();
        }
        else{
            pattern = Pattern.compile("[нН]ачальник\\s+[оО]тдела\\s+([оО][пП])\\s+[иИ]\\s+([уУ][чЧ][рР])");
            matcher = pattern.matcher(item);
            if (matcher.find()){
                profStart = matcher.start();
                arr[4] = profStart;
                profEnd = matcher.end();
                arr[5] = profEnd;
                item = item.substring(0, profStart) + item.substring(profEnd);
                return matcher.group();
            }
            else{
                return "";
            }
        }
    }

    private String getType(){
        String regex = "((\\s+([лЛ]\\S{1,4}[цЦ][иИ]*\\S{1,2}[яЯиИ])|([пП]\\S{1,4}[кК]\\S{1,2}[иИ]\\S{1,2}[аА]))+)(?:\\S*\\s*((([пП]\\S{1,4}[кК]\\S{1,2}[иИ]\\S{1,2}[аА])|([лЛ]\\S{1,4}[цЦ][иИ]*\\S{1,2}[яЯ]))+))?|(([лЛ]\\S+[рР]\\S+[рР]\\S+[аАыЫ][еЕяЯ]\\s*[рР]\\S+[бБ]\\S+[аАыЫ]))";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()){
            typeStart = matcher.start();
            arr[6] = typeStart;
            typeEnd = matcher.end();
            arr[7] = typeEnd;
            item = item.substring(0, typeStart) + item.substring(typeEnd);
            return matcher.group();
        }
        else{
            return "";
        }
    }

    private String getTitle(){
        int min = getMin();
        String tmp = item.substring(0, min);
        item = item.substring(min);
        return tmp;
    }

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