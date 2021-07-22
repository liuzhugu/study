package org.liuzhugu.javastudy.book.logicjava.generic;

import java.util.Random;

public class Test {

    public static void main(String[] args) {
//        Pair<String,Integer> pair = new Pair<>("liuzhugu",1);
//        //类型检查 提前报错
//        //Integer id = pair.getFirst();
//        //String value = pair.getSecond();
//
//        //动态数组
//        DynamicArray<Double> arr = new DynamicArray<>();
//        Random random = new Random();
//        int size = random.nextInt(100);
//        for (int i = 0;i < size;i ++) {
//            arr.add(Math.random());
//        }
//        System.out.println(arr.get(random.nextInt(size)));

        //方法不需要提前声明类型 每次都可以传入不同类型
//        makePair("liuzhugu",1);
//        makePair(1,"liuzhugu");

//        //子类实现接口时声明了类型 那么子类不需要参数实例化了
//        ChildTwo childTwo = new ChildTwo();
//        childTwo.sayHello(" liuzhugu");
//        //子类不声明类型  因此使用子类时 还是需要传参数类型
//        ChildOne<String> first = new ChildOne<>();
//        first.sayHello("liuzhugu");
//        ChildOne<Integer> second = new ChildOne<>();
//        second.sayHello(1);

//        NumberPair<Integer,Double> pair = new NumberPair<>(10,12.34);
//        System.out.println(pair.sum());
//
//        DynamicArray<Number> numbers = new DynamicArray<>();
//        DynamicArray<Integer> integers = new DynamicArray<>();
//        integers.add(100);
//        integers.add(34);
//        numbers.addAll(integers);

        DynamicArray<Number> numbers = new DynamicArray<>();
        DynamicArray<Integer> integers = new DynamicArray<>();
        integers.add(100);
        integers.add(34);
        integers.copyTo(numbers);

    }


    public static <U,V> Pair<U,V> makePair(U first,V second) {
        Pair<U,V> pair = new Pair<>(first,second);
        return pair;
    }
}
