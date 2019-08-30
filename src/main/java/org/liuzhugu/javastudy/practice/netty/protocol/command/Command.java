package org.liuzhugu.javastudy.practice.netty.protocol.command;

public interface Command {

    Byte LOGIN_REQUEST = 1;
    Byte LOGIN_RESPONSE = 2;
    Byte LOGOUT_REQUEST = 3;
    Byte LOGOUT_RESPONSE = 4;
    Byte MESSAGE_REQUEST = 5;
    Byte MESSAGE_RESPONSE = 6;
    Byte CREATE_GROUP_REQUEST = 7;
    Byte CREATE_GROUP_RESPONSE = 8;
}
