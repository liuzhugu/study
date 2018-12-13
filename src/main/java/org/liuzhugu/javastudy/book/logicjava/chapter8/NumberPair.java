package org.liuzhugu.javastudy.book.logicjava.chapter8;

public class NumberPair<U extends Number,V extends Number> extends Pair<U,V>{
    public NumberPair(){}
    public NumberPair(U first,V second){
        super(first,second);
    }
    public double sum(){
        return getFirst().doubleValue()+getSecond().doubleValue();
    }
}
