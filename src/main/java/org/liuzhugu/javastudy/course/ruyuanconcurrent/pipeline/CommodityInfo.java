package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

import java.math.BigDecimal;

/**
 * 商品信息
 * */
public class CommodityInfo {

    /**
     * 商品id
     * */
    private long id;


    /**
     * 商品名称
     * */
    private String name;

    /**
     * 商品价格
     * */
    private BigDecimal price;

    /**
     * 商品主图地址
     * */
    private String mainUrl;

    /**
     * 商品产地
     * */
    private String address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
