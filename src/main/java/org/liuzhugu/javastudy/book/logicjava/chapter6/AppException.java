package org.liuzhugu.javastudy.book.logicjava.chapter6;

/**
 * Created by liuting6 on 2018/2/24.
 */
public class AppException extends Exception {
    public AppException(){
        super();
    }
    public AppException(String message){
        super(message);
    }
    public AppException(String message,Throwable cause){
        super(message,cause);
    }
    public AppException(Throwable cause){
        super(cause);

    }
}
