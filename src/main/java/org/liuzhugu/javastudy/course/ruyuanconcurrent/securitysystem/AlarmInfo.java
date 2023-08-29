package org.liuzhugu.javastudy.course.ruyuanconcurrent.securitysystem;

public class AlarmInfo {
    /**
     * 楼号
     */
    private Integer no;

    /**
     * 几单元
     */
    private Integer unit;

    /**
     * 几零几
     */
    private String roomNumber;

    /**
     * 报警类型
     */
    private Integer alarmType;

    public AlarmInfo(Integer no, Integer unit, String roomNumber, Integer alarmType) {
        this.no = no;
        this.unit = unit;
        this.roomNumber = roomNumber;
        this.alarmType = alarmType;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    @Override
    public String toString() {
        return "AlarmInfo{" +
                "no=" + no +
                ", unit=" + unit +
                ", roomNumber='" + roomNumber + '\'' +
                ", alarmType=" + alarmType +
                '}';
    }
}
