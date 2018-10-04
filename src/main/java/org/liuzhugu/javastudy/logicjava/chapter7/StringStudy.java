package org.liuzhugu.javastudy.logicjava.chapter7;

import java.nio.charset.Charset;

/**
 * Created by liuting6 on 2018/2/28.
 */
public class StringStudy {
    public static void main(String[] args){
        String name="老马说编程";
        name=new String("老马说编程");
        name="老马";
        name+="说编程";
        String description=",探索编程本质";
        System.out.println(name+description);
        String str="hello,world";
        String[] strings=str.split(",");
        System.out.println(strings[1]);
        System.out.println(Charset.defaultCharset().name());
        Charset charset=Charset.forName("gbk");
        System.out.println(charset.name());
        System.out.println("liuzhugu".length());
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("老马说编程");
        stringBuilder.append("，享受编程的乐趣");
        System.out.println(stringBuilder.toString());
    }
}
