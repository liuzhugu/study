package org.liuzhugu.javastudy.course.ruyuanconcurrent.threadpool;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class SmsVerificationCodeTask implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SmsVerificationCodeTask.class);

    private long phoneNumber;

    public SmsVerificationCodeTask(long phoneNumber) {
        Preconditions.checkArgument(String.valueOf(phoneNumber).length() == 11,
                "phoneNumber length must be 11!");
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void run() {
        //生成验证码
        int verificationCode = ThreadLocalRandom.current().nextInt(999999);
        DecimalFormat df = new DecimalFormat("000000");
        String txtVerificationCode = df.format(verificationCode);
        //发送短信
        sendMessage(phoneNumber,txtVerificationCode);
    }

    private void sendMessage(long phoneNumber,String txtVerificationCode) {
        System.out.println("发送短信开始:phoneNumber -> " + phoneNumber + ",txtVerificationCode -> " + txtVerificationCode);
        try {
            //模拟网络调用
            Thread.sleep(500);
        } catch (Exception e) {
        }
        System.out.println("发送短信结束:phoneNumber -> " + phoneNumber + ",txtVerificationCode -> " + txtVerificationCode);
    }
}
