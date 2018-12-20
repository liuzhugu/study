package org.liuzhugu.javastudy.book.logicjava.heap;

import lombok.Data;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.PriorityQueue_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.Queue_;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 优先级队列
 * */
@Data
class Student{
    private String name;
    private int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString(){
        return name+" "+score;
    }


}

public class PriorityQueueStudy {

    public static void main(String[] args){
       Queue_<Student> students=new PriorityQueue_<Student>(new Comparator<Student>() {
           @Override
           public int compare(Student o1, Student o2) {
               //最小堆
               //return o1.getScore()-o2.getScore();
               //最大堆
               return o2.getScore()-o1.getScore();

           }
       });
       students.add(new Student("张三",80));
       students.add(new Student("李四",85));
       students.add(new Student("王五",61));
       students.add(new Student("刘六",70));
//       while(students.peek()!=null){
//           System.out.println(students.poll());
//       }
        //输出最大值
        System.out.println("max is:"+students.peek());

        //输出前2大
        System.out.print("输出前2大:");
        for(int i=0;i<2;i++){
            System.out.print(students.poll()+" ");
        }
        System.out.println();

        //输出第3大
        int i=0;
        System.out.print("输出第3大:");
        while(students.peek()!=null){
            if(i++==3){
                System.out.println(students.poll());
                break;
            }
        }

        //TopK
    }
}
