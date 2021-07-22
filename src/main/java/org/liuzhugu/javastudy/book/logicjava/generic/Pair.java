package org.liuzhugu.javastudy.book.logicjava.generic;

public class Pair<U ,V> {
    U first;
    V second;
    public Pair(U first,V second) {
        this.first = first;
        this.second = second;
    }

    public U getFirst(){
        return first;
    }

    public V getSecond(){
        return second;
    }

}
