package org.liuzhugu.javastudy.course.ruyuanconcurrent.advertisesystem;

import org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination.AlarmType;

/**
 * 广告费报警信息
 * */
public class AlarmInfo {

    /**
     * 告警id
     */
    private String id;

    /**
     * 广告主
     */
    private String advertiser;

    /**
     * 广告主联系方式
     */
    private String phoneNumber;

    /**
     * 告警类型 {@link AlarmType}
     */
    private Integer alarmType;

    public AlarmInfo(String id, String advertiser, String phoneNumber, Integer alarmType) {
        this.id = id;
        this.advertiser = advertiser;
        this.phoneNumber = phoneNumber;
        this.alarmType = alarmType;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AlarmInfo{" +
                "id='" + id + '\'' +
                ", advertiser='" + advertiser + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", alarmType=" + alarmType +
                '}';
    }

    public String getUniqueIdByAlarmType(AlarmType alarmType) {
        return this.getId() + "-" + alarmType;
    }

}
