package org.liuzhugu.javastudy.practice.netty.study.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.liuzhugu.javastudy.practice.netty.study.attribute.Attributes;

public class LoginUtil {

    //可以通过给 channel 绑定属性来设置某些状态，获取某些状态，不需要额外的 map 来维持
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> loginAttr = channel.attr(Attributes.LOGIN);

        return loginAttr.get() != null;
    }
}

