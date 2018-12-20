package org.liuzhugu.javastudy.practice.reflection;

public class ClassTest {

    public static void main(String[] args)throws Exception{

        //获取class
        //1.通过forName方式获取
        Class c1=Class.forName("org.liuzhugu.javastudy.practice.reflection.Employee");
        //2.直接通过类获取class对象
        Class c2= Employee.class;
        Employee employee=new Employee("小明","18","读书",9000,"开发",100);
        //3.通过对象获取
        Class c3=employee.getClass();
        if(c1==c2&&c1==c3){
            System.out.println("c1、c2、c3 为同一个对象");
            System.out.println(c1);
        }


    }
}
