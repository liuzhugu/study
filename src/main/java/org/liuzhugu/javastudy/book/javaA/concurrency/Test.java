package org.liuzhugu.javastudy.book.javaA.concurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<List<String>> datas = new ArrayList<>();
        List<Map<String, String>> result = datas.stream().map(data -> {
            Map<String, String>  dataMap = new HashMap<String, String>();
            return dataMap;
        }).collect(Collectors.toList());
    }
}
