package org.liuzhugu.javastudy.course.ruyuanconcurrent.masterslave;

/**
 * 日志信息
 * */
public class LogInfo {
    /**
     * 日志级别
     * */
    private String level;
    /**
     * 操作类型
     * */
    private String operationType;
    /**
     * 商品id
     * */
    private long commodityId;
    /**
     * 操作时间
     * */
    private String timestamp;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "level='" + level + '\'' +
                ", operationType='" + operationType + '\'' +
                ", commodityId=" + commodityId +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
