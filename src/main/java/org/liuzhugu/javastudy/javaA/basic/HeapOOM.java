package org.liuzhugu.javastudy.javaA.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuting6 on 2018/1/3.
 */
public class HeapOOM {
    public static void main(String[] args){
        List<String> list=new ArrayList<String>();
        while(true){
            list.add("内存溢出呀，内存溢出呀");
        }
    }
}
