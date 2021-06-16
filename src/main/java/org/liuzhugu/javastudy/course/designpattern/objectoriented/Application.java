package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private List<Filter> filters = new ArrayList<>();

    public void handleRequest(RpcRequest req) {
        try {
            for (Filter filter : filters) {
                filter.doFilter(req);
            }
        } catch (RpcException e) {
            //...处理筛选结果
        }
        //...省略其他处理逻辑...
    }
}
