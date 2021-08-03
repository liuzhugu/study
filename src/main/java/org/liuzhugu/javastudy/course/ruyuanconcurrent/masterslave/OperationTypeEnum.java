package org.liuzhugu.javastudy.course.ruyuanconcurrent.masterslave;

/**
 * 数据库的操作类型
 * */
public enum  OperationTypeEnum {

    SELECT("select", "查询"),
    UPDATE("update", "更新"),
    DELETE("delete", "删除"),
    ;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 描述
     */
    private String desc;

    OperationTypeEnum(String operationType, String desc) {
        this.operationType = operationType;
        this.desc = desc;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
