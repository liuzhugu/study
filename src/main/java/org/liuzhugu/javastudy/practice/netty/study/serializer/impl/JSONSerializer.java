package org.liuzhugu.javastudy.practice.netty.study.serializer.impl;

import com.alibaba.fastjson.JSON;
import org.liuzhugu.javastudy.practice.netty.study.serializer.Serializer;
import org.liuzhugu.javastudy.practice.netty.study.serializer.SerializerAlogrithm;

public class JSONSerializer implements Serializer{

    @Override
    public byte getSerializerAlogrithm() {
        return SerializerAlogrithm.JSON;
    }

    @Override
    public byte[] serialize(Object object){
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz,byte[] bytes){
        return JSON.parseObject(bytes,clazz);
    }
}