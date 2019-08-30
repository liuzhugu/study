package org.liuzhugu.javastudy.book.logicjava.heap;

import lombok.Data;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.queue.PriorityQueue_;

import java.util.Collection;

@Data
public class TopK<E> {
    private int k;
    private PriorityQueue_<E> queue;


    public TopK(int k) {
        this.k = k;
        queue=new PriorityQueue_<>(k);
    }

    public void addAll(Collection<? extends E> collection){
        for(E e:collection){
            add(e);
        }
    }

    public void add(E e){
        if(queue.size()==k){
            Comparable<? super E> head=(Comparable<? super E>)queue.peek();
            if(head.compareTo(e)>0){
                //小于TopK中的最小值,不用变
                return;
            }else {
                //删除最小值,加入该值
                queue.poll();
                queue.add(e);
            }
        }else {
            queue.add(e);
        }
    }

    public <T> T[] toArray(T[] t){
        return queue.toArray(t);
    }

    public E getKth(){
        return queue.peek();
    }
}
