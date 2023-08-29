package org.liuzhugu.javastudy.course.ruyuanconcurrent.immutable;

import java.util.HashMap;
import java.util.Map;

public class CarLocationTracker {

    /**
     * 车辆编码对应车辆位置信息map
     * */
    private Map<String, Location> locationMap = new HashMap<>();

    /**
     * 更新车辆位置
     *
     * @param carCode  车辆编码
     * @param newLocation 位置信息
     * */
    public void updateLocation(String carCode,Location newLocation) {
        locationMap.put(carCode,newLocation);
    }

    /**
     * 获取车辆位置
     *
     * @param carCode  车辆编码
     * */
    public Location updateLocation(String carCode) {
        return locationMap.get(carCode);
    }
}
