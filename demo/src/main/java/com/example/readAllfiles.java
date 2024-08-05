package com.example;

import java.io.File;

public class readAllfiles {
    public readAllfiles(){
        readDir();
    }

    private void readDir(){
        File dir = new File("xlsx/");
        for (String item : dir.list()){
            readFiles(dir + "/" + item);
        }
    }

    private void readFiles(String filePath){
        File dir = new File(filePath);
        for (File item : dir.listFiles()){
            String fileRelativePath = "xlsx/";
            fileRelativePath += dir.getName() + "/" + item.getName();

            new fillDBWithThis(fileRelativePath, dir.getName());
        }
    }
}