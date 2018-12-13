package org.liuzhugu.javastudy.book.thinkinginjava.reflection;

/**
 * Created by liuting6 on 2017/12/27.
 */
class Cookie{
    static{
        System.out.println("loading Cookie!");
    }
}
class Candy{
    static {
        System.out.println("loading Candy");
    }
}
class Gum{
    static{
        System.out.println("loading Gum");
    }
}
public class SweetShop {
    public static void main(String[] args){
//        System.out.println("inner main");
//        new Candy();
//        System.out.println("after creating candy");
//        try{
//            Class.forName("Gum");
//        }catch (ClassNotFoundException e){
//            e.printStackTrace();
//        }
//        System.out.println("after class.forName(\"Gum\")");
//        new Cookie();
//        System.out.println("after creating cookie");
        System.out.println(double.class);
        System.out.println(Double.TYPE);
        //字节码指令一样，都是Class类
        System.out.println(double.class==Double.TYPE);
    }
}
