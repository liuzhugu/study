package org.liuzhugu.javastudy.practice.za;

import java.util.Scanner;

public class AIMain {

    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        String str;
        while(scanner.hasNext()){
            str = scanner.next();
            str = str.replace("你","我");
            str = str.replace("吗","");
            str = str.replace("?","!");
            str = str.replace("？","!");
            System.out.println(str);
        }
    }
}
