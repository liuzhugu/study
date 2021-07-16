package org.liuzhugu.javastudy.practice.za;


import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkTest {
    public static void main(String[] args) {
        try {
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            WorkTest workTest = new WorkTest();
            workTest.test();
        }
    }
//    void test() {
//        try {
//            int a = 1 / 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            e.getMessage();
//        }
//    }
    void test() {
        int a = 1 / 0;
    }


}
