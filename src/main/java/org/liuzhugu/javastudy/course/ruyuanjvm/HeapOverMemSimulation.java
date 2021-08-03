package org.liuzhugu.javastudy.course.ruyuanjvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟堆溢出
 * */
public class HeapOverMemSimulation {
    public static void main(String[] args) {
        long count = 0;
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
            System.out.println("当前创建了第 " + (++count) + " 个对象");
        }
    }
}
