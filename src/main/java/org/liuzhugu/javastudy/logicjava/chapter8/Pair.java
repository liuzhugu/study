package org.liuzhugu.javastudy.logicjava.chapter8;

import java.io.Serializable;

public class Pair<U,V> {
    private U first;
    private V second;

    public Pair(){}
    public Pair(U first,V second){
        this.first=first;
        this.second=second;
    }
//    public double sum(){
//        return getFirst().doubleValue()+getSecond().doubleValue();
//    }
    public U getFirst() {
        return first;
    }

    public void setFirst(U first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    @Override
    public String toString(){
        return "[first=" + this.first + ", second=" + this.second + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return second != null ? second.equals(pair.second) : pair.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
