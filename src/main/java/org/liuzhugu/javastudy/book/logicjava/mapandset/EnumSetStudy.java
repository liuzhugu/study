package org.liuzhugu.javastudy.book.logicjava.mapandset;

import lombok.Data;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.EnumSet_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.Set_;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


enum Day{
    MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY
}

@Data
class Worker{
    String name;
    Set_<Day> availableDays;

    public Worker(String name, Set_<Day> availableDays) {
        this.name = name;
        this.availableDays = availableDays;
    }
}

public class EnumSetStudy {

    public static void main(String[] args){
        //基于位向量实现
        //抽象类,调其工厂方法创建对象
        //Set_<Day> weekend = EnumSet_.noneOf(Day.class);

        Worker[] workers=new Worker[]{
                new Worker("张三", EnumSet_.of(Day.MONDAY,Day.THURSDAY,Day.SUNDAY)),
                new Worker("李四", EnumSet_.of(Day.MONDAY,Day.FRIDAY,Day.SATURDAY)),
                new Worker("王五", EnumSet_.of(Day.MONDAY,Day.TUESDAY,Day.SUNDAY))
        };


        //那一天不会有人来
        Set_<Day> day1 = EnumSet_.allOf(Day.class);
        for(Worker worker:workers){
            day1.removeAll(worker.availableDays);
        }
        System.out.println("不会有人来的天有:"+day1);

        //那一天会至少有一个人来
        Set_<Day> day2 = EnumSet_.noneOf(Day.class);
        for(Worker worker:workers){
            day2.addAll(worker.availableDays);
        }
        System.out.println("至少有一个人来的天有:"+day2);

        //那一天所有人都会来
        Set_<Day> day3 = EnumSet_.allOf(Day.class);
        for(Worker worker:workers){
            day3.retainAll(worker.availableDays);
        }
        System.out.println("所有人都会来的天有:"+day3);

        //周一和周二都会来的人有
        List<Worker> workerList=new ArrayList<>();
        for(Worker worker:workers){
            if(worker.availableDays.containsAll(EnumSet_.of(Day.MONDAY,Day.SUNDAY))){
                workerList.add(worker);
            }
        }
        System.out.print("周一和周五都来的人有:");
        for(Worker worker:workerList){
            System.out.print(worker.name+" ");
        }
        System.out.println();


    }
}
