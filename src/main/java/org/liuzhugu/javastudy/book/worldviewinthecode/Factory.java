package org.liuzhugu.javastudy.book.worldviewinthecode;

import java.util.ArrayList;
import java.util.List;

public class Factory {
    static Factory sharedFactory = new Factory();
    public static List<Car> productCars() {
        List<Car> cars = new ArrayList<>();
        //取巧地在静态方法中访问普通方法,但其实看起来跟静态方法是一样的
        cars.add(sharedFactory.productCar());
        cars.add(sharedFactory.productCar());
        return cars;
    }
    public Car productCar() {
        return new Car();
    }
    public static void main(String[] args) {
        productCars();
    }
}
class Car{}