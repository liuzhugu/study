package org.liuzhugu.javastudy.practice.tool;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * 雪花算法
 * */
public class SnowFlake {
    private static final int UNUSED_BITS = 1;
    private static final int EPOCH_BITS = 41;
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;


    private static final long DEFAULT_CUSTOM_EPOCH = 1420070400000L;

    private final long nodeId;
    private final long customerEpoch;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    //初始化需要传入节点ID和年代
    public SnowFlake(long nodeId,long customerEpoch) {
        if (nodeId < 0 || nodeId > maxNodeId) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d"));
        }
        this.nodeId = nodeId;
        this.customerEpoch = customerEpoch;
    }

    public SnowFlake(long nodeId) {
        this(nodeId,DEFAULT_CUSTOM_EPOCH);
    }

    public SnowFlake() {
        this.nodeId = createNodeId();
        this.customerEpoch = DEFAULT_CUSTOM_EPOCH;
    }

    //获取下一个ID
    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalArgumentException("Invalid System Clock");
        }

        //同一时间戳   我们需要递增序号
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                //如果序号用尽  则需要等到下一毫秒继续执行
                currentTimestamp = waitNextMills(currentTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        //当前时间戳  + 机器节点 + 序号 组成ID
        long id = currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS)
                | (nodeId << SEQUENCE_BITS)
                | sequence;
        return id;
    }

    //默认基于mac地址生成节点ID
    private long createNodeId() {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte macPort : mac) {
                        sb.append(String.format("%02X%",macPort));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & maxNodeId;
        return nodeId;
    }

    private long timestamp() {
        return Instant.now().toEpochMilli() - customerEpoch;
    }

    //由于这样被耗尽的情况不多   且需要等待的时间也只有1ms   所以选择死循环进行阻塞
    private long waitNextMills(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }
}
