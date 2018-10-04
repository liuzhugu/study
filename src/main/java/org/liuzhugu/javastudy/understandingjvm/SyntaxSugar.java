package org.liuzhugu.javastudy.understandingjvm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuting6 on 2017/11/8.
 * 语法糖
 */
public class SyntaxSugar {
    public static void main(String[] args){

//        List<Integer> list1= Arrays.asList(1,2,3,4);
//        //List<Integer> list2={1,2,3,4};
//        int sum=0;
//        for(int i:list1){
//            sum+=i;
//        }
//        System.out.println(sum);
        //去除语法糖之后，即编译之后
        //泛型，                                             变长参数，自动装箱
        List<Integer> list=Arrays.asList(new Integer[]{Integer.valueOf(1),Integer.valueOf(2),Integer.valueOf(3),Integer.valueOf(4)});
        int sum=0;
        //遍历循环
        for(Iterator iterator=list.iterator();iterator.hasNext();){
            //自动拆箱
            int i=((Integer)iterator.next()).intValue();
            sum+=i;
        }
        System.out.println(sum);
    }

}
