package com.media.intellisensemedia.utils;

import java.util.Date;

public class Utilities {


    public static String getFormatedDuration(String string) {
        long length = Long.parseLong(string);
        int temp = (int) (length / 1000);
        int minutes = temp / 60;
        int seconds = temp % 60;
        return String.format("%02d : %02d", minutes, seconds);
    }

    public static String getFormattedDate(String input){
        return (new Date(Long.parseLong(input))).toLocaleString();
    }

    public static String getFormattedViews(String input){
        return input;
    }

    public static String getFormattedTag(String location){
        String fina = "";
        for(char ch :  location.toCharArray()){
            fina = fina + ch+"/";
        }
        return fina;
    }
}
