package org.liuzhugu.javastudy.framestudy.spring.ioc;

import org.springframework.core.io.*;

public class SpringRun {

    public static void main(String[] args)throws Exception{
        //xml load
//        ApplicationContext context=new FileSystemXmlApplicationContext("D:\\liuting6_svn\\javastudy\\src" +
//                "\\main\\jdk8\\org\\liuzhugu\\javastudy\\spring\\ioc\\applicationContext.xml");
//        Animal animal=(Animal) context.getBean("animal");
//        animal.say();

        //resource study
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        Resource fileResource1 = resourceLoader.getResource("D:\\liuting6_svn\\javastudy\\src" +
                "\\main\\jdk8\\org\\liuzhugu\\javastudy\\spring\\ioc\\applicationContext.xml");
        System.out.println("fileResource1 is FileSystemResource:" + (fileResource1 instanceof FileSystemResource));

        Resource fileResource2 = resourceLoader.getResource("\\liuting6_svn\\javastudy\\src" +
                "\\main\\jdk8\\org\\liuzhugu\\javastudy\\spring\\ioc\\applicationContext.xml");
        System.out.println("fileResource2 is ClassPathResource:" + (fileResource2 instanceof ClassPathResource));

        Resource urlResource1 = resourceLoader.getResource("file:\\liuting6_svn\\javastudy\\src" +
                "\\main\\jdk8\\org\\liuzhugu\\javastudy\\spring\\ioc\\applicationContext.xml");
        System.out.println("urlResource1 is UrlResource:" + (urlResource1 instanceof UrlResource));

        Resource urlResource2 = resourceLoader.getResource("http://www.baidu.com");
        System.out.println("urlResource1 is urlResource:" + (urlResource2 instanceof UrlResource));
    }
}
