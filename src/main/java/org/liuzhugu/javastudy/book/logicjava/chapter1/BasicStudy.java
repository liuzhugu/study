package org.liuzhugu.javastudy.book.logicjava.chapter1;

/**
 * Created by liuting6 on 2018/1/29.
 */
public class BasicStudy {
    public static void main(String[] args){
//        Scanner scanner=new Scanner(System.in);
//        System.out.println("please input password");
//        String passWord="5678";
//        while(scanner.hasNext()){
//            if(passWord.equals(scanner.next())){
//                System.out.println("login success!");
//                break;//跳出循环
//            }else {
//                System.out.println("password wrong!please input again!");
//            }
//        }
//        System.out.println("welcome!");
//        int goal=111,i=0;
//        for(;i<100;i++){
//            if(i==goal){
//                break;
//            }
//        }
//        if(i==goal){
//            System.out.println("found");
//        }
//        if(i!=goal){
//            System.out.println("not found");
//        }
//        int sum=0;
//        for (int i=0;i<1000;i++){
//            sum++;
//            for(int j=i;j<i*i;j++){
//                sum++;
//                if(j%i==0){
//                    sum++;
//                }
//            }
//        }
//        System.out.println(sum);
//        int[] arr={10,20,30};
//        reset(arr);
//        for(int i=0;i<arr.length;i++){
//            System.out.println(arr[i]);
//        }
        //变长参数列表
//        System.out.println("max is："+max(1));
//        System.out.println("max is："+max(1,3,5));
//        System.out.println("max is："+max(1,3,5,7));
//        System.out.println("max is："+max(1,3,5,7,9));
//        m1("");
//        m1("aaa");
//        m1("aaa", "bbb");//第一个参数被s接收，多余的才被弄成数组让ss接收
        //System.out.println(-2-Integer.MAX_VALUE);
        //System.out.println(-0b1111111);
        System.out.println((-(Integer.MAX_VALUE+1))<<1); //左移，右边补0,这里是头部表示负数的1被移没了
        System.out.println((-(Integer.MAX_VALUE+1))>>>1);//右移，左边补0，这里是头部补了代表正数的0
        System.out.println((-(Integer.MAX_VALUE+1))>>1); //右移，左边按情况补0或者是1，符号保持不变
    }
    public static void m1(String s, String... ss) {
        for (int i = 0; i < ss.length; i++) {
            System.out.println(ss[i]);
        }
    }
    public static int max(int first,int ...all){
        //可变参数列表变成一个数组
        int max=first;
        for(int i=1;i<all.length;i++){
            if(all[i]>max){
                max=all[i];
            }
        }
        return max;
    }
    public static void reset(int[] arr){
        for(int i=0;i<arr.length;i++){
            arr[i]=i;
        }
    }
}
