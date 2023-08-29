package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

/**
 * Description:中断顶层接口
 **/
public interface Termination {
    /**
     * 请求终止线程
     */
    void terminate();
}
