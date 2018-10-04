package org.liuzhugu.javastudy.javaA.basic;

/**
 * Created by liuting6 on 2018/1/4.
 */
import java.util.ArrayList;
import java.util.List;

public class StringOomMock {
    static String base = "string";
    public static void main(String[] args) {
        List list = new ArrayList();
        for (int i=0;i< Integer.MAX_VALUE;i++){
            String str = base + base;
            base = str;
            list.add(str.intern());
        }
    }
}
