package org.liuzhugu.javastudy.practice.concurrent;

import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.ThreadLocal_;
import org.liuzhugu.javastudy.sourcecode.jdk8.concurrent.Thread_;

public class ThreadLocalDemo {
    public static void main(String[] args) {
        //ThreadLocal封装了根据当前线程操作的过程
        ThreadLocal_<String> name = new ThreadLocal_<>();
        //完整语义应该是name.get(当前线程);
        name.get();
    }
}

