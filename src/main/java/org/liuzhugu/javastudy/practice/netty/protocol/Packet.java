package org.liuzhugu.javastudy.practice.netty.protocol;

import lombok.Data;

@Data
//数据包
public abstract class Packet {

    //数据包得有版本之分
    private Byte version=1;

    /**
     * @return 各个结构实现该方法来通过指令标示自己是什么对象，在序列化和反序列化时调用
     * */
    public abstract Byte getCommand();
}
