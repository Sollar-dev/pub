package com.example;

public class states {
    public int id;
    public String group;
    public boolean weak;
    public short day;
    public String text;
    public String course;

    // для уровня образования
    public String courseName;
    public int courseCode;

    public String num;
    public String secName;
    public String aud;
    public String prof;
    public String type;
    public String title;
    public String extra;

    public states(int id, String course, String group, boolean weak, short day, String text){
        this.id = id;
        this.course = course;
        this.group = group;
        this.weak = weak;
        this.day = day;
        this.text = text;
    }

    public states(boolean weak, short day, String num, String secName, String aud, String prof, String type, String title, String extra){
        this.weak = weak;
        this.day = day;
        this.num = num;
        this.secName = secName;
        this.aud = aud;
        this.prof = prof;
        this.type = type;
        this.title = title;
        this.extra = extra;
    }

    public states(String course, String group, boolean weak, short day, String text){
        this.course = course;
        this.group = group;
        this.weak = weak;
        this.day = day;
        this.text = text;
    }

    public states(boolean weak, short day, String text){
        this.weak = weak;
        this.day = day;
        this.text = text;
    }

    public states(String name, boolean isCourse){
        if (isCourse == true){
            this.courseName = name;
        }
        else{
            this.group = name;
        }
    }

    @Override
    public String toString(){
        return String.format("id: %s | course: %s | group: %s | weak: %s | day: %s | text: %s", this.id, this.course, this.group, this.weak, this.day, this.text);
    }
}
