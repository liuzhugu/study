package org.liuzhugu.javastudy.course.ruyuanjvm;

public class Demo1 {
    /**
     * 模拟新生代垃圾回收
     * */
    public static void main(String[] args) {
        byte[] array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = null;

        byte[] array2 = new byte[128 * 1024];

        //内存不够分配   触发第一次Young GC
        byte[] array3 = new byte[2 * 1024 * 1024];

        //模拟第二次Young GC
        array3 = new byte[2 * 1024 * 1024];
        array3 = new byte[2 * 1024 * 1024];
        array3 = new byte[128 * 1024];
        array3 = null;
        array3 = new byte[2 * 1024 * 1024];
    }
}
