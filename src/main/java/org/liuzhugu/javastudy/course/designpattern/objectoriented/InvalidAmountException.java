package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public class InvalidAmountException extends Exception{
    private String message;
    public InvalidAmountException(String message) {
        this.message = message;
    }
}
