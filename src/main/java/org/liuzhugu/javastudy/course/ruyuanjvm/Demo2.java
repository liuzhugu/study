package org.liuzhugu.javastudy.course.ruyuanjvm;

public class Demo2 {

    /**
     * 模拟新生代对象达到15之后进入老年代的情况
     * */
    public static void main(String[] args) {
        //有700K的未知对象
        byte[] array3 = new byte[200 * 1024];
        //15时  没有对象进入老年代   16时  array3和其他一直存活的700k未知对象进入老年嗲
        for (int i = 0;i < 15;i ++) {
            byte[] array1 = new byte[13 * 1024 * 1024];
            byte[] array2 = new byte[528 * 1024];
            array1 = null;
            //内存不够分配  触发垃圾回收
            byte[] array4 = new byte[528 * 1024];

        }
    }
}
