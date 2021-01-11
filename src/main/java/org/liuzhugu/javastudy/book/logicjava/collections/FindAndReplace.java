package org.liuzhugu.javastudy.book.logicjava.collections;

import java.util.*;

public class FindAndReplace {
    public static void main(String[] args) {
        //二分查找   java会把int装箱为Integer   但不会把int[] 变为Integer[]
        List<Integer> list1 = new ArrayList<Integer>(Arrays.asList(new Integer[]{35,25,13,12,8,5,1,5}));
        List<Integer> list2 = new ArrayList<Integer>(Arrays.asList(new Integer[]{3}));

        System.out.println(Collections.binarySearch(list1,10,Collections.reverseOrder()));

        System.out.println(Collections.max(list1));
        System.out.println(Collections.frequency(list1,5));
        System.out.println(Collections.disjoint(list1,list2));

        List<Integer> test = new ArrayList<Integer>(Arrays.asList(new Integer[]{1,2,3,4,5,6,7}));
        Collections.rotate(test.subList(1,4),-1);
        System.out.println(test);

        List<String> list3 = new ArrayList<>(Arrays.asList("liuzhugu"));
    }
}
