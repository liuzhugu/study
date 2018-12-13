package org.liuzhugu.javastudy.book.javaA.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuting6 on 2018/1/3.
 */
public class GCOverHead {
    public final static byte[] DEFAULT_BYTES=new byte[12*1024*1024];

    public static void main(String[] args){
        List<byte[]> temp=new ArrayList<byte[]>();
        while(true){
            temp.add(new byte[1024*1024]);
            if(temp.size()>3){
                temp.clear();
            }
        }
    }
}
