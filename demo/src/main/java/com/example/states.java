package com.example;

public class states {
    public int id;
    public String group;
    public boolean weak;
    public short day;
    public String text;

    public states(String group, boolean weak, short day, String text){
        this.group = group;
        this.weak = weak;
        this.day = day;
        this.text = text;
    }

    public states(int id, String group, boolean weak, short day, String text){
        this.id = id;
        this.group = group;
        this.weak = weak;
        this.day = day;
        this.text = text;
    }

    @Override
    public String toString(){
        return String.format("id: %s | group: %s | weak: %s | day: %s | text: %s", this.id, this.group, this.weak, this.day, this.text);
    }
}
