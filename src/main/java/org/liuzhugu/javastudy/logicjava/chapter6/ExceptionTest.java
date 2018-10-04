package org.liuzhugu.javastudy.logicjava.chapter6;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by liuting6 on 2018/2/23.
 */
public class ExceptionTest {
    public static void main(String[] args)throws Exception{
        //空指针
//        String s=null;
//        s.indexOf("a");
//        System.out.println("end");
        //数字格式化
//        if(args.length<1){
//            System.out.println("请输入数字");
//            return;
//        }
//        int num=Integer.parseInt(args[0]);
//        System.out.println(num);
        //自己捕获异常
//        try{
//            int num=Integer.parseInt(args[0]);
//            System.out.println(num);
//        }catch (NumberFormatException e){
//            System.out.println("参数 "+args[0]+" 不是有效数字，请输入数字");
//            throw new AppException("输入格式不正确");
//        }catch (Exception e){
//            e.printStackTrace();
//            throw e;
//        }
//        try {
//            new ExceptionTest().test();
//        }catch (Exception e){
//            System.out.println("main中捕获异常");
//            System.out.println(e.getCause());
//            e.printStackTrace();
//
//        }
        try{
            new ExceptionTest().test();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("main 捕获异常");
        }
    }
    public void test()throws Exception{
        String[] args={"a"};
        try{
//            int num=Integer.parseInt(args[0]);
//            System.out.println(num);
            test1();
        }catch (NumberFormatException e){
            System.out.println("参数 "+args[0]+" 不是有效数字，请输入数字1");
            throw new AppException("输入格式不正确");
        }catch (Exception e){
            System.out.println("test中捕获异常");
            e.printStackTrace();
            throw e;
        }
    }
    public void test1()throws AppException,SQLException,NumberFormatException{
//        int ret=0;
//        try{
//            int a=5/0;
//            return ret;
//        }finally {
//            //return在finally之后才执行，但finally无法改变return的结果
//            //ret=2;
//            //不仅覆盖try和catch中的return，而且还掩盖了向上传递的异常
//            //return 2;
//        }
        String[] args={"a"};
        try{
            int num=Integer.parseInt(args[0]);
            System.out.println(num);
        }catch (NumberFormatException e){
            System.out.println("test1中捕获异常");
            throw new AppException("输入格式不正确");
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

    }
    public void tester()throws AppException{
        try{
            test1();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
