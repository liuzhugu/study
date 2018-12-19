package org.liuzhugu.javastudy.book.logicjava.classexpansion;

/**
 * Created by liuting6 on 2018/2/8.
 */
public abstract class AbstractAdder implements IAdd{
    @Override
    public void addAll(int[] num){
        for(int i:num){
            add(i);
        }
    }
}
