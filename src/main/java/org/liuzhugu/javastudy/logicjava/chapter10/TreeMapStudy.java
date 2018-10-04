package org.liuzhugu.javastudy.logicjava.chapter10;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeMapStudy {
    public static void main(String[] args){

        //set Comparator
        //Map<String,String> map=new TreeMap<>();
//        Map<String,String> map=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        //Map<String,String> map=new TreeMap<>(Collections.<String>reverseOrder());
        //Map<String,String> map=new TreeMap<>(Collections.<String>reverseOrder(String.CASE_INSENSITIVE_ORDER));
//        Map<String,String> map=new TreeMap<>(new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.compareTo(o2);
//            }
//        });
//        Map<String,String> map=new TreeMap<>(new Comparator<String>() {
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//            @Override
//            public int compare(String o1, String o2) {
//                try {
//                    return sdf.parse(o1).compareTo(sdf.parse(o2));
//                }catch (ParseException e){
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//        });


        //set String value
//        map.put("T","tree");
//        map.put("a","abstract");
//        map.put("b","basic");
//        map.put("c","call");
//        map.put("t","try");

        //set date value
//        map.put("2018-09-01","100");
//        map.put("2018-08-13","100");
//        map.put("2018-08-2","100");
//
//        for(Map.Entry<String,String> entry:map.entrySet()){
//            System.out.println("[key:"+entry.getKey()+",value:"+entry.getValue()+"]");
//        }

        String productName="[春节]<酒店和门票>hello";
        //获取节日
        String festival="",name="";
        Pattern  festivalPattern= Pattern.compile("(\\[[^\\]]*\\])");
        Matcher festivalMatchar = festivalPattern.matcher(productName);
        if(festivalMatchar.find()){
            festival=festivalMatchar.group().substring(1,festivalMatchar.group().length()-1);
        }

        //获取产品名
        Pattern  namePattern= Pattern.compile("(\\<[^\\]]*\\>)");
        Matcher nameMatcher = namePattern.matcher(productName);
        if(nameMatcher.find()){
            name=nameMatcher.group().substring(1,nameMatcher.group().length()-1);
        }


        System.out.println(festival+" "+name);
    }
}
