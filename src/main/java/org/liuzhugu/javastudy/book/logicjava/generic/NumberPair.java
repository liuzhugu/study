package org.liuzhugu.javastudy.book.logicjava.generic;

public class NumberPair<U extends Number,V extends Number> extends Pair<U,V>{
    public NumberPair(U first,V second) {
        super(first,second);
    }

    public double sum() {
        return getFirst().doubleValue() + getSecond().doubleValue();
    }
}
