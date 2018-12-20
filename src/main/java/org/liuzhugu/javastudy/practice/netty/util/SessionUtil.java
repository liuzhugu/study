package org.liuzhugu.javastudy.practice.netty.util;


import io.netty.channel.Channel;
import org.liuzhugu.javastudy.practice.netty.attribute.Attributes;
import org.liuzhugu.javastudy.practice.netty.common.Session;

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

    public static Channel getChannel(Integer userId){
        return userIdChannelMap.get(userId);
    }

}
