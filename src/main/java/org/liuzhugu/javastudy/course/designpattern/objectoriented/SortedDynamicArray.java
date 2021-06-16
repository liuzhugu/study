package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public class SortedDynamicArray extends DynamicArray {


    //复用其他代码   只需要对不满意的地方修改  就可以尽可能复用了现有代码
    @Override
    public void add(Integer e) {
        ensureCapacity();
        int i;
        //保证数组有序
        for (i = size - 1;i >= 0;i --) {
            if (elements[i] > e) {
                elements[i + 1] = elements[i];
            } else {
                break;
            }
        }
        elements[i + 1] = e;
        size ++;
    }
}
