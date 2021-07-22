package org.liuzhugu.javastudy.practice.rpc.complex.network.future;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求ID和Reponse的映射
 */
public class SyncWriteMap {
    public static Map<String,WriteFuture> syncKey = new ConcurrentHashMap<String,WriteFuture>();
}
