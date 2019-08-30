package org.liuzhugu.javastudy.book.javaA.concurrency;

/**
 * Created by liuting6 on 2018/1/12.
 * 程序无法中断
 */
public class NoVisitabilityTest {
    private static class ReadThread extends Thread{
         private boolean ready;
        private int number;
        public void run(){
            while(!ready){
                number++;
            }
            System.out.println(ready);
        }
        public void readyOn(){
            //当main方法调用这个修改了ready，但run方法却不会去主存读取ready的最新值，所以不会结束,除非把其设置为volatile
            this.ready=true;
        }
    }
    public static void main(String[] args)throws InterruptedException{
        ReadThread readThread=new ReadThread();
        readThread.start();
        Thread.sleep(200);
        readThread.readyOn();
        System.out.println(readThread.number);
    }
}
