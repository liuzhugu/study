package org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SmsRouter {

    /**
     * 短信网关对象,通过volatile保证可见性
     * */
    private static volatile SmsRouter instance = new SmsRouter();

    /**
     * 短信服务商信息的map  key表示服务的优先级
     * */
    private final Map<Integer,SmsInfo> smsInfoRouteMap;

    /**
     * 获取短信网关对象
     * */
    public static SmsRouter getInstance() {
        return instance;
    }

    /**
     * 短信服务商列表变更后,更新短信网关
     *
     * @param newInstance
     * */
    public static void setInstance(SmsRouter newInstance) {
        instance = newInstance;
    }

    /**
     * 初始化短信网关路由信息
     * */
    public SmsRouter() {
        this.smsInfoRouteMap = this.loadSmsInfoRouteMapFromDb();
    }

    public Map<Integer, SmsInfo> getSmsInfoRouteMap() {
        //return smsInfoRouteMap;
        //进行防御性编程   返回的是副本 外界无法通过该引用影响不可变类
        return Collections.unmodifiableMap(deepCopy(smsInfoRouteMap));
    }

    private  Map<Integer, SmsInfo> deepCopy( Map<Integer, SmsInfo> smsInfoRouteMap) {
        Map<Integer,SmsInfo> result = new HashMap<>(smsInfoRouteMap.size());
        for (Map.Entry<Integer,SmsInfo> entry : smsInfoRouteMap.entrySet()) {
            result.put(entry.getKey(),entry.getValue());
        }
        return result;
    }

    /**
     * 从数据库加载短信服务商信息
     * */
    private Map<Integer,SmsInfo> loadSmsInfoRouteMapFromDb() {
        //初始化  模拟db的数据
        Map<Integer,SmsInfo> routeMap = new HashMap<>();
        routeMap.put(1,db.get(1));
        routeMap.put(2,db.get(2));
        routeMap.put(3,db.get(3));
        return routeMap;
    }

    /**
     * 短信服务商列表变更
     * */
    public void changeRouteInfo() {
//        Map<Integer,SmsInfo> smsInfoMap = instance.getSmsInfoRouteMap();
//        SmsInfo smsInfo = smsInfoMap.get(3);
//        //这两个操作并不是原子性操作  如果设置了url还没来得及设置第二项时   其他线程读取了该配置
//        //那么读取到错误信息
//        smsInfo.setUrl("https://www.jiguang.cn");
//        smsInfo.setMaxSizeInBytes(183L);

        //修改为不变类后 直接整个替换 这样就不会有中间状态了
        updateSmsRouteInfoList();
        //直接创建一个新的   创建好新的整个替换掉 确保所有更新操作在设置变量之前完成 那么就不会有中间状态了
        SmsRouter.setInstance(new SmsRouter());
    }

    //模拟数据库数据
    private static Map<Integer,SmsInfo> db = new HashMap<>(3);

    static {
        db.put(1,new SmsInfo(1L,"https://www.aliyun.com",180L));
        db.put(2,new SmsInfo(2L,"https://cloud.tencent.com",181L));
        db.put(3,new SmsInfo(3L,"https://cloud.baidu.com",182L));
    }

    private void updateSmsRouteInfoList() {
        db.put(2,new SmsInfo(2L,"https://www.jiguang.cn",183L));
    }
}
