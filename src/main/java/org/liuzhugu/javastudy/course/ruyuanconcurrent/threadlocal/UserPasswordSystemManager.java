package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadlocal;

import java.util.concurrent.*;

public class UserPasswordSystemManager {
    /**
     * 单例对象
     * */
    private static final UserPasswordSystemManager INSTANCE = new UserPasswordSystemManager();
    private UserPasswordSystemManager(){};
    public static UserPasswordSystemManager getInstance() {
        return INSTANCE;
    }

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1,
            Runtime.getRuntime().availableProcessors() * 2,
            60,
            TimeUnit.SECONDS,
            //任务队列  没有缓冲区 每个任务都会创一条线程来执行
            new SynchronousQueue<>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r,"register-thread-");
                    thread.setDaemon(true);
                    return thread;
                }
            });

    /**
     * 注册用户
     */
    public void register(String loginName,String phoneNumber) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                //随机生成一个用户的6位数密码
                ThreadSpecificSecureRandom random = ThreadSpecificSecureRandom.getInstance();
                StringBuilder passwordBuilder = new StringBuilder();
                for (int i = 0;i < 6;i ++) {
                    passwordBuilder.append(random.nextInt(10));
                }
                String initPassword = passwordBuilder.toString();
                //注册用户
                saveUser(loginName,phoneNumber,initPassword);
                //发送短信
                sendMessage(loginName,phoneNumber,initPassword);
            }
        };
        EXECUTOR.submit(task);
    }

    /**
     *  保存用户
     */
    private void saveUser(String loginName,String phoneNumber,String initPassword) {
        System.out.println("保存登陆账号: " + loginName + ",手机号:" + phoneNumber + ",密码:" + initPassword);
    }

    /**
     * 发送短信
     * */
    private void sendMessage(String loginName,String phoneNumber,String initPassword) {
        //发送短信
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("登陆账号: " + loginName + ",手机号:" + phoneNumber + ",密码:" + initPassword);
    }
}
