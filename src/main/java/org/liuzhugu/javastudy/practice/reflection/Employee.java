package org.liuzhugu.javastudy.practice.reflection;

public class Employee extends Person {
    private int salary;
    protected String position;
    public int empNo;
    public static Integer totalNum=0;

    public void sayHello(){
        System.out.println(String.format("Hello, 我是 %s, 今年 %s 岁, 爱好是%s, 我目前的工作是%s, 月入%s元\n", name, age, getHobby(), position, salary));
    }

    private void work(){
        System.out.println(String.format("My name is %s, 工作中勿扰.", name));
    }



    public Employee(String name, String age, String hobby, int salary, String position, int empNo) {
        super(name, age, hobby);
        this.salary = salary;
        this.position = position;
        this.empNo = empNo;
        Employee.totalNum++;
    }

    public Employee(){}
}