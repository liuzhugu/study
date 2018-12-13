package org.liuzhugu.javastudy.practice.netty.serializer;


import org.liuzhugu.javastudy.practice.netty.serializer.impl.JSONSerializer;

//将序列化提取出来，可以灵活替换
public interface Serializer {

    Serializer DEFAULT=new JSONSerializer();

    byte getSerializerAlogrithm();

    byte[] serialize(Object object);

    <T> T deserialize(Class<T> clazz,byte[] bytes);
}
