package org.liuzhugu.javastudy.practice.netty.protocol;

import io.netty.buffer.ByteBuf;
import org.liuzhugu.javastudy.practice.netty.protocol.command.Command;
import org.liuzhugu.javastudy.practice.netty.protocol.request.LoginRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.request.MessageRequestPacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.LoginResponsePacket;
import org.liuzhugu.javastudy.practice.netty.protocol.response.MessageResponsePacket;
import org.liuzhugu.javastudy.practice.netty.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;

import org.liuzhugu.javastudy.practice.netty.serializer.impl.JSONSerializer;

public class PacketCodeC {

    //魔数
    public  static final int MAGIC_NUMBER=0x12345678;

    public static final PacketCodeC INSTANCE=new PacketCodeC();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;

    public PacketCodeC(){
        packetTypeMap=new HashMap<>();
        //传入类型枚举值，取相应的class
        packetTypeMap.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(Command.LOGIN_RESPONSE, LoginResponsePacket.class);
        packetTypeMap.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);
        packetTypeMap.put(Command.MESSAGE_RESPONSE, MessageResponsePacket.class);

        serializerMap=new HashMap<>();
        Serializer serializer=new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlogrithm(),serializer);

    }

    //序列化
    public void encode(ByteBuf byteBuf,Packet packet){

        // 1. 序列化 jdk8 对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);


        //依次组装数据包,顺序:魔数-版本-算法-指令-长度-数据
        //其中算法和指令可以灵活替换，算法决定序列化方法，指令决定序列化对象类型
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlogrithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    //反序列化
    public Packet decode(ByteBuf byteBuf){
        //跳过魔数
        byteBuf.skipBytes(4);

        //暂时不处理版本
        byteBuf.skipBytes(1);

        //序列化算法
        byte serializeAlgorithm=byteBuf.readByte();

        // 指令
        byte command=byteBuf.readByte();

        // 数据包长度
        int length=byteBuf.readInt();

        //数据
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        //根据算法和指令获取对象类型和序列化方法
        Class<? extends Packet> requestType=getRequestType(command);
        Serializer serializer=getSerializer(serializeAlgorithm);
        if(requestType!=null&&serializer!=null){
            //反序列化
            return serializer.deserialize(requestType,bytes);
        }
        return null;
    }

    public Serializer getSerializer(byte serializeAlgorithm){
        return serializerMap.get(serializeAlgorithm);
    }

    public Class<? extends Packet> getRequestType(byte command){
        return packetTypeMap.get(command);
    }

}
