package org.liuzhugu.javastudy.logicjava.chapter1;

/**
 * Created by liuting6 on 2018/1/26.
 */
public class ArrayStudy {
    public static void main(String[] args){
        byte b=123;
        short s=12345;
        int i=1234567890;
        long l=1234567891011121314L;//超出int以后必须带L
        float f=1.3f;//默认为double，一个double赋给float会丢失精度，因此必须加上f
        double d=1.3;
        char c='刘';//必须''不能""，可以用整型值赋值
        int[] arr1={1,3,5,7,9};
        int[] arr2=new int[]{1,3,5,7,9};
        int[] arr3=new int[3];   //未在初始化时知道确切数据，那么必须声明长度
        //int[] arr4=new int[3]{1,3,5};报错
        long l1=Integer.MAX_VALUE*2;//溢出，int相乘还是int
        long l2=Integer.MAX_VALUE*2L;//因为有更高一级的数据类型，因此低的那方也被提升为相同的运算
        String str='刘'+"注孤";
        double d2=10/4.0;
        //System.out.println(d2);
        int test=1;
        switch (test){
            case 1:
                System.out.println(1);
            case 2:
                System.out.println(2);
                break;
            case 3:
                System.out.println(3);
                break;
            default:
                System.out.println(4);
        }
    }
}
