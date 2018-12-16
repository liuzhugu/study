package org.liuzhugu.javastudy.practice.netty.study.util;


import io.netty.channel.Channel;
import org.liuzhugu.javastudy.practice.netty.study.attribute.Attributes;
import org.liuzhugu.javastudy.practice.netty.study.common.Session;

import java.util.HashMap;
import java.util.Map;

public class SessionUtil {

    //userId -> channelId的映射
    private static final Map<Integer,Channel> userIdChannelMap = new HashMap<>();

    public static void bindSession(Session session, Channel channel) {
        userIdChannelMap.put(session.getUserId(),channel);
        channel.attr(Attributes.SESSION).set(session);
    }

    public static void unBindSession(Channel channel){
        if(hasLogin(channel)){
            userIdChannelMap.remove(getSession(channel).getUserId());
            channel.attr(Attributes.SESSION).set(null);
        }
    }

    public static boolean hasLogin(Channel channel){
       return channel.hasAttr(Attributes.SESSION);
    }

    public static Session getSession(Channel channel){
        return channel.attr(Attributes.SESSION).get();
    }

    public static Channel getChannel(String userId){
        return userIdChannelMap.get(userId);
    }

}
