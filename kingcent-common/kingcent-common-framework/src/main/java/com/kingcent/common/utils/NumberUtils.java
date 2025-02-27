package com.kingcent.common.utils;

import java.util.ArrayList;
import java.util.List;

public class NumberUtils {
    public static List<Integer> splitInt(String numbers, String regex, boolean emptyIntoNull){
        if(numbers == null) return null;
        List<Integer> list = new ArrayList<>();
        for (String s : numbers.split(regex)) {
            try{
                list.add(Integer.parseInt(s));
            }catch (Exception ignored){}
        }
        if(emptyIntoNull && list.isEmpty()){
            return null;
        }
        return list;
    }

    public static List<Long> splitLong(String numbers, String regex, boolean emptyIntoNull){
        if(numbers == null) return null;
        List<Long> list = new ArrayList<>();
        for (String s : numbers.split(regex)) {
            try{
                list.add(Long.parseLong(s));
            }catch (Exception ignored){}
        }
        if(emptyIntoNull && list.isEmpty()){
            return null;
        }
        return list;
    }
}
