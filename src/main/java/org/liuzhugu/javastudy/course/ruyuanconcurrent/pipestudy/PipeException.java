package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

public class PipeException extends Exception {
    private static final long serialVersionUID = -2944728968269016114L;

    /**
     * 抛出异常的Pipe
     * */
    private Pipe<?,?> sourcePipe;

    /**
     * 抛出异常的参数
     * */
    private Object input;

    public PipeException(Pipe<?,?> sourcePipe,Object input,String message) {
        super(message);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }

    public PipeException(Pipe<?,?> sourcePipe,Object input,String message,Throwable cause) {
        super(message,cause);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }
}
