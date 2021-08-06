package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipeline;

/**
 * 流水线
 * 对复合Pipe的抽象  一个Pipeline实例可以包含多个Pipe实例
 *
 * */
public interface Pipeline<IN,OUT> extends Pipe<IN,OUT> {

    /**
     * 往该Pipeline实例中添加一个Pipe实例
     *
     * @param pipe  Pipe实例
     * */
    void addPipe(Pipe<?,?> pipe);
}
