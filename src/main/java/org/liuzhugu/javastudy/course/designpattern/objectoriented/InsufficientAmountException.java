package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public class InsufficientAmountException extends Exception{
    private String message;
    public InsufficientAmountException(String message) {
        this.message = message;
    }
}
