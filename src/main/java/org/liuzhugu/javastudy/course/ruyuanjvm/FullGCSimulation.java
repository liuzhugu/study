package org.liuzhugu.javastudy.course.ruyuanjvm;

public class FullGCSimulation {
    /**
     * 模拟Full GC
     * */
    public static void main(String[] args) {
        //过大对象直接进入老年代
        byte[] array1 = new byte[4 * 1024 * 1024];

        array1 = null;

        //占满新生代   并且survivor放不下存活对象 因此触发Full GC
        byte[] array2 = new byte[2 * 1024 * 1024];
        byte[] array3 = new byte[2 * 1024 * 1024];
        byte[] array6 = new byte[2 * 1024 * 1024];
        byte[] array5 = new byte[508 * 1024];



        //回收之后   在新声代分配对象
        byte[] array7 = new byte[2 * 1024 * 1024];
    }
}
