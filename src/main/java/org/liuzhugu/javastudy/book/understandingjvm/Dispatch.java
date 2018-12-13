package org.liuzhugu.javastudy.book.understandingjvm;

/**
 * Created by liuting6 on 2017/10/25.
 * 单分派和多分派
 */
public class Dispatch {
    static class QQ{}
    static class _360 extends QQ{}
    static class Father{
        public void hardChoice(QQ qq){
            System.out.println("father choose qq!");
        }
        public void hardChoice(_360 _360){
            System.out.println("father choose 360!");
        }
    }
    static class Son extends Father{
        public void hardChoice(QQ qq){
            System.out.println("son choose qq!");
        }
//        public void hardChoice(_360 _360){
//            System.out.println("son choose 360!");
//        }
    }
    public static void main(String[] args){
        Father father=new Father();
        Father son=new Son();
        //father choose qq!
        father.hardChoice(new QQ());
        //father choose 360!
        son.hardChoice(new _360());
        //son choose qq!   因为被该方法被子类覆盖了
        son.hardChoice(new QQ());
    }
}
