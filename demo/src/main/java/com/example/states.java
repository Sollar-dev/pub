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

    public states(int id, String course, String group, boolean weak, short day, String text){
        this.id = id;
        this.course = course;
        this.group = group;
        this.weak = weak;
        this.day = day;
        this.text = text;
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

    public states(String courseName){
        this.courseName = courseName;
    }

    @Override
    public String toString(){
        return String.format("id: %s | course: %s | group: %s | weak: %s | day: %s | text: %s", this.id, this.course, this.group, this.weak, this.day, this.text);
    }
}
