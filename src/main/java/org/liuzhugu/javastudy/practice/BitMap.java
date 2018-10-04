package org.liuzhugu.javastudy.practice;

import org.liuzhugu.javastudy.logicjava.chapter8.Pair;

import java.util.ArrayList;
import java.util.List;

public class BitMap {
    private static final int BITSPERWORD = 32; // 整数位数
    private static final int SHIFT = 5;
    private static final int MASK = 0x1F; // 5位遮蔽 0B11111
    private static final int N = 100000;

    private static int[] bitSort(int[] array) {
        // 用int数组来模拟位数组，总计(1 + N / BITSPERWORD)*BITSPERWORD位，足以容纳N
        int[] result = new int[(1 + N / BITSPERWORD)];
        for (int i = 0; i < N; i++)
            clr(result,i); // 位数组所有位清0
        for (int i = 0; i < array.length; i++)
            set(result,array[i]); // 阶段2
        return result;
    }

    // 置a[i>>SHIFT]的第(i & MASK)位为1，也就是位数组的第i位为1
    private static void set(int[] a,int i) {
        a[i >> SHIFT] |= (1 << (i & MASK));
    }

    // 置a[i>>SHIFT]的第(i & MASK)位为0,也就是位数组的第i位为0
    private static void clr(int[] a,int i) {
        a[i >> SHIFT] &= ~(1 << (i & MASK));
    }

    // 测试位数组的第i位是否为1
    private static boolean test(int[] a,int i) {
        return (a[i >> SHIFT] & (1 << (i & MASK))) == (1 << (i & MASK));
    }

    public static List<Integer> getNums(int[] array){
        List<Integer> nums=new ArrayList<>();
        if(array==null||array.length==0){
            return nums;
        }
        for (int i = 0; i < N; i++)
            if (test(array,i))
                nums.add(i);
        return nums;
    }

    //位图&
    public static  List<Integer> and(int[] first,int[] second){
        if(first==null||first.length==0||second==null||second.length==0){
            return new ArrayList();
        }
        //转换成位图
        int[] firstBitMap=bitSort(first),secondBitMap=bitSort(second);
        int[] result = new int[(1 + N / BITSPERWORD)];
        for(int i=0;i<result.length;i++){
            //与
            result[i]=firstBitMap[i]&secondBitMap[i];
        }
        //从位图转换回来
        return getNums(result);
    }

    //位图|
    public static  List<Integer> or(int[] first,int[] second){
        if(first==null||first.length==0||second==null||second.length==0){
            return new ArrayList();
        }
        //转换成位图
        int[] firstBitMap=bitSort(first),secondBitMap=bitSort(second);
        int[] result = new int[(1 + N / BITSPERWORD)];
        for(int i=0;i<result.length;i++){
            //或
            result[i]=firstBitMap[i]|secondBitMap[i];
        }
        //从位图转换回来
        return getNums(result);
    }
}