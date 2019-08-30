package org.liuzhugu.javastudy.practice.reflection;

import java.util.ArrayList;

public class ObjectAnalyzerTest {

    public static void main(String[] args){
        int size = 4;
        ArrayList<Integer> squares = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            squares.add(i * i);
        }

        ObjectAnalyzer analyzer=new ObjectAnalyzer();
        //输出数组
        System.out.println(analyzer.toString(squares));
        //输出String
        System.out.println(analyzer.toString("string"));
        //输出对象
        System.out.println(analyzer.toString(new Employee("张三","18","读书",9000,"开发",21900)));
    }
}
