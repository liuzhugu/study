package org.liuzhugu.javastudy.logicjava.chapter9;

import java.io.Serializable;
import java.util.*;

public class ArrayListStudy {
    public static void main(String[] args){
//        ArrayList<String> strings=new ArrayList<>();
//        strings.add("liu");
//        strings.add("zhugu");
        //first
//        for(String str:strings){
//            System.out.println(str);
//        }
        //second
//        for(int i=0;i<strings.size();i++){
//            System.out.println(strings.get(i));
//        }
        //third
//        Iterator<String> iterator=strings.iterator();
//        while(iterator.hasNext()){
//            System.out.println(iterator.next());
//        }
        //ListIterator
//        ListIterator iterator=strings.listIterator(strings.size());
//        while(iterator.hasNext()){
//            System.out.println(iterator.next());
//        }
//        while (iterator.hasPrevious()){
//            System.out.println(iterator.previous());
//        }
//        List<Integer> integers=new ArrayList<>();
//        for(int i=0;i<100;i++){
//            integers.add(i);
//        }
//        for(String str:strings){
//            strings.remove(str);
//        }
//        try {
//            for(Integer i:integers){
//                if(i>50){
//                    integers.remove(i);
//                }
//            }
//            Iterator<Integer> iterator=integers.iterator();
//            while (iterator.hasNext()){
//                int i=iterator.next();
//                if(i>50){
//                    iterator.remove();
//                }
//            }
//            integers.size();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        ArrayList<Integer> intList=new ArrayList<>();
        intList.add(123);
        intList.add(456);
        intList.add(789);
        Integer[] arrA=new Integer[3];
        Integer[] arrB=new Integer[2];
        intList.toArray(arrA);
        intList.toArray(arrB);
        Integer[] arrC=intList.toArray(arrB);
        //
        System.out.println(Arrays.equals(arrA,arrB));
        System.out.println(Arrays.equals(arrB,arrC));
        System.out.println(Arrays.equals(arrA,arrC));
    }
}
