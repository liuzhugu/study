package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 商品数据库查询
 * */
public class CommodityDbData {
    /**
     * 商品数量
     * */
    private AtomicInteger commodityNumber = new AtomicInteger();
    /**
     * 是否有下一条
     * */
    public boolean hasNext() {
        return commodityNumber.get() <= 2;
    }
    /**
     * 获取下一条数据
     * */
    public CommodityInfo next() {
        return createCommodity(commodityNumber.getAndIncrement());
    }

    private CommodityInfo createCommodity(int commodityId) {
        CommodityInfo commodityInfo = new CommodityInfo();
        commodityInfo.setId(commodityId);
        commodityInfo.setName("测试产品-" + commodityId);
        commodityInfo.setPrice(BigDecimal.TEN);
        commodityInfo.setMainUrl("www.baidu.com");
        commodityInfo.setAddress("上海");
        return commodityInfo;
    }
}
