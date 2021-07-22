package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadlocal;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 每个线程获取自己的随机数生成器
 * */
public class ThreadSpecificSecureRandom {
    /**
     * 单例对象
     * */
    private static final ThreadSpecificSecureRandom INSTANCE = new ThreadSpecificSecureRandom();
    private ThreadSpecificSecureRandom(){};

    public static ThreadSpecificSecureRandom getInstance() {
        return INSTANCE;
    }

    /**
     * 定义 线程本地存储SecureRandom对象
     * */
    private static final ThreadLocal<SecureRandom> SECURE_RANDOM_THREAD_LOCAL = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            SecureRandom secureRandom = null;
            try {
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                secureRandom = new SecureRandom();
            }
            return secureRandom;
        }
    };

    /**
     * 随机生成一个数
     * */
    public int nextInt(int bound) {
        SecureRandom random = SECURE_RANDOM_THREAD_LOCAL.get();
        return random.nextInt(bound);
    }
}
