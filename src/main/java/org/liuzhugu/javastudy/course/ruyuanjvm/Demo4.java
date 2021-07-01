package org.liuzhugu.javastudy.course.ruyuanjvm;

public class Demo4 {
    /**
     * 模拟动态年龄判断
     * */
    public static void main(String[] args) {
        //令三次以后  存活的对象年龄1 + 2 + 3大于survivor的一半   然后3进入老年代
        byte[][] arrays = new byte[5][];
        for (int i = 0;i < 5;i ++) {
            //让array里的元素为每次存活下来的对象
            arrays[i] = new byte[667 * 1024];
            byte[] array1 = new byte[30 * 1024 * 1024];
            array1 = null;
            byte[] array2 = new byte[3 * 1024 * 1024];
        }
    }
}
