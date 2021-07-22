package org.liuzhugu.javastudy.practice.concurrent;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class ThreadStudy {

//    public static void main(String[] args) throws Exception{
//
//        String htmlEmailTemplate = "您的验证码:test "+"</br>此验证码30分钟内有效，请及时输入。</br> linking you and me";
//        String forgetEmail =  "lting@china-lehua.com";
//
//        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//
//
//        //创建一个配置文件并保存
//        Properties properties = new Properties();
//
//        properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
//        properties.setProperty("mail.smtp.port", "465");
//        properties.setProperty("mail.smtp.socketFactory.port", "465");
//        properties.setProperty("mail.smtp.auth", "true");
//        properties.put("mail.smtp.host","smtp.exmail.qq.com");
//        properties.put("mail.smtp.username", "987685625@qq.com");
//        properties.put("mail.smtp.password", "oxlwywfbywihbcad");
//
//
//        //QQ存在一个特性设置SSL加密
//        MailSSLSocketFactory sf = new MailSSLSocketFactory();
//        sf.setTrustAllHosts(true);
//        properties.put("mail.smtp.ssl.enable", "true");
//        properties.put("mail.smtp.ssl.socketFactory", sf);
//
//        //创建一个session对象
//        Session emailSession = Session.getDefaultInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("987685625@qq.com", "oxlwywfbywihbcad");
//            }
//        });
//
//        //开启debug模式
//        emailSession.setDebug(false);
//
//        //获取连接对象
//        Transport transport = emailSession.getTransport();
//
//        //连接服务器
//        transport.connect("smtp.exmail.qq.com", "987685625@qq.com", "oxlwywfbywihbcad");
//
//        //创建邮件对象
//        MimeMessage mimeMessage = new MimeMessage(emailSession);
//
//        //邮件发送人
//        mimeMessage.setFrom(new InternetAddress("987685625@qq.com"));
//
//        //邮件接收人
//        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(forgetEmail));
//
//        //邮件标题
//        mimeMessage.setSubject("重置密码");
//
//        //邮件内容
//        mimeMessage.setContent(htmlEmailTemplate, "text/html;charset=UTF-8");
//
//        //发送邮件
//        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
//
//        //关闭连接
//        transport.close();
//    }

    public static void main(String[] args) throws AddressException,MessagingException {

        // 创建Properties 类用于记录邮箱的一些属性
        Properties props = new Properties();
        // 表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        //此处填写SMTP服务器
        props.put("mail.smtp.host", "smtp.qq.com");
        //端口号，QQ邮箱端口587
        props.put("mail.smtp.port", "587");
        // 此处填写，写信人的账号
        props.put("mail.user", "987685625@qq.com");
        // 此处填写16位STMP口令
        props.put("mail.password", "tsaprsuzamvxbcca");

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(form);

        // 设置收件人的邮箱
        InternetAddress to = new InternetAddress("lting@china-lehua.com");
        message.setRecipient(Message.RecipientType.TO, to);

        // 设置邮件标题
        message.setSubject("邮件测试");

        // 设置邮件的内容体
        message.setContent("test", "text/html;charset=UTF-8");

        // 最后当然就是发送邮件啦
        Transport.send(message);


    }

}
