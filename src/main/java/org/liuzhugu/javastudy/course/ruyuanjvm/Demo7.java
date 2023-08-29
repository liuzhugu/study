package org.liuzhugu.javastudy.course.ruyuanjvm;

import java.util.ArrayList;
import java.util.List;

public class Demo7 {
    public static void main(String[] args) throws Exception{
        List<Data> datas = new ArrayList<>();
        for (int i = 0;i < 10000;i ++) {
            datas.add(new Data());
        }
        Thread.sleep(1 * 60 * 60 * 1000);
    }
    static class Data {

    }
}
