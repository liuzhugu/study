package org.liuzhugu.javastudy.logicjava.chapter10;

import org.liuzhugu.javastudy.logicjava.chapter8.Pair;

import java.util.*;

public class HashMapStudy {
    public static void main(String[] args){
        //count appear time
//        Random random=new Random();
//        HashMap<Integer,Integer> numCount=new HashMap<>();
//        for(int i=0;i<1000;i++){
//            int num=random.nextInt(10);
//            if(numCount.containsKey(num)){
//                int count=numCount.get(num);
//                numCount.put(num,count+1);
//            }else {
//                numCount.put(num,1);
//            }
//        }
//        for(Map.Entry<Integer,Integer> entry:numCount.entrySet()){
//            System.out.println("num "+entry.getKey()+" appear "+entry.getValue()+" times!");
//        }

        //set
        Set<Pair<String,String>> pairs=new HashSet<>();
        pairs.add(new Pair<String, String>("1","2"));
        pairs.add(new Pair<String, String>("1","2"));
        pairs.add(new Pair<String, String>("1","2"));
        pairs.add(new Pair<String, String>("1","2"));
        pairs.add(new Pair<String, String>("1","2"));
        System.out.println(pairs);
    }
}
