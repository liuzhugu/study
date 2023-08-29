package org.liuzhugu.javastudy.course.ruyuanjvm;

public class BigObjectSimulation {

    /**
     * 模拟大对象直接进入老年代的情况
     * */
    public static void main(String[] args) {
        //新生代有2M的未知对象

        //新分配的3M过大  直接进入老年代
        byte[] array1 = new byte[3 * 1024 * 1024];
    }
}
