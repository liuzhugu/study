package org.liuzhugu.javastudy.framestudy.spring.proxy.study;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class App {

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("file:D:\\liuting6_svn\\javastudy\\src\\main\\java\\org\\liuzhugu\\javastudy\\framestudy\\spring\\proxy\\study\\beans.xml");
        IUser iUser = (IUser)ac.getBean("proxy");
        iUser.save();
    }
}
