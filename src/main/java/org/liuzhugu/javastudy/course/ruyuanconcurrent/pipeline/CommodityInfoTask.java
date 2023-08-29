package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

/**
 * 商品详细信息静态化的任务
 * */
public class CommodityInfoTask {
    /**
     * 商品信息
     * */
    public final CommodityInfo commodityInfo;

    /**
     * 生成的静态页面的文件名称
     * */
    public final String targetFileName;

    public CommodityInfoTask(CommodityInfo commodityInfo, String targetFileName) {
        this.commodityInfo = commodityInfo;
        this.targetFileName = targetFileName;
    }
}
