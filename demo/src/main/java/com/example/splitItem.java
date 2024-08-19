package com.example;

import java.util.ArrayList;
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
        item = Item.toLowerCase();
    }

    public ArrayList<String> getSplitItem(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(getNum());
        result.add(getSecName());
        result.add(getAUD());
        result.add(getProf());
        result.add(getType());
        result.add(getTitle());
        result.add(item);
        return result;
    }

    private String getNum(){
        String tmp = item.substring(0, 1);
        item = item.substring(1);
        return tmp;
    }

    private String getSecName(){
        Pattern pattern = Pattern.compile("\\S*\\s*\\D[.]\\D[.]");
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()){
            secNameStart = matcher.start();
            arr[0] = secNameStart;
            secNameEnd = matcher.end();
            arr[1] = secNameEnd;
            item = item.substring(0, secNameStart) + item.substring(secNameEnd);
            return matcher.group();
        }
        else{
            return "";
        }
    }

    private String getAUD(){
        Pattern pattern = Pattern.compile("([аА][уУ][дД]\\D?\\s*\\S*)|(([сСчЧ]\\S*[оОиИ]\\S*[тТ]\\S*[ыЫ]?[йЙ]?\\s*)([зЗ][аА][лЛ]))", Pattern.CASE_INSENSITIVE);
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
            return "";
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
            return "";
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
