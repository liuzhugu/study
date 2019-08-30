package org.liuzhugu.javastudy.book.javaA.concurrency;

import java.util.Random;

/**
 * Created by liuting6 on 2018/1/12.
 */
public class ThreadJoinTest {
    static class Computer extends Thread{
        private int[] array;
        private int start;
        private int end;
        private int result;
        public Computer(int[] array,int start,int end){
            this.array=array;
            this.start=start;
            this.end=end;
        }

        @Override
        public void run(){
            for(int i=start;i<end;i++){
                result+=array[i];
                if(result<0)result&=Integer.MAX_VALUE;
            }
        }
        public int getResult(){
            return result;
        }
    }
    private final static int COUNTER=10000000;
    public static void main(String[] args)throws InterruptedException{
        int[] array=new int[COUNTER];
        Random random=new Random();
        for(int i=0;i<COUNTER;i++){
            array[i]=Math.abs(random.nextInt());
        }
        long start=System.currentTimeMillis();
        Computer c1=new Computer(array,0,COUNTER);
        //Computer c2=new Computer(array,COUNTER/2,COUNTER);
        //Computer c3=new Computer(array,COUNTER/3*2,COUNTER);
        c1.start();
        //c2.start();
        //c3.start();
        c1.join();
        //c2.join();
        //c3.join();
        System.out.println(System.currentTimeMillis()-start);
        System.out.println((c1.getResult())&Integer.MAX_VALUE);
    }
}
