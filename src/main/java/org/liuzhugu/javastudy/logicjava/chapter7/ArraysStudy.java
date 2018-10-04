package org.liuzhugu.javastudy.logicjava.chapter7;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by liuting6 on 2018/2/28.
 */
public class ArraysStudy {
    public static void main(String[] args){
        int[] arr={9,8,3,4};
        Arrays.sort(arr);
        System.out.println(Arrays.toString(arr));
        //String[] strings={"1","hello","2","world"};
        //Arrays.sort(strings);
        String[] strings={"hello","world","Break","abc"};
//        Arrays.sort(strings, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o2.compareToIgnoreCase(o1);
//            }
//        });
        Arrays.sort(strings, Collections.<String>reverseOrder(String.CASE_INSENSITIVE_ORDER));
        System.out.println(Arrays.toString(strings));
        int[] arr1={3,5,7,23,31};
        System.out.println(Arrays.binarySearch(arr1,13));
        int[][] arr2=new int[][]{{0,1},{2,3,4},{5,6,7,8}};
        System.out.println(Arrays.deepToString(arr2));
    }
}
