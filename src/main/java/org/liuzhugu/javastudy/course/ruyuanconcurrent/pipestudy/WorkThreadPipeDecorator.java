package org.liuzhugu.javastudy.course.ruyuanconcurrent.pipestudy;

import java.util.concurrent.TimeUnit;

public class WorkThreadPipeDecorator<IN,OUT> implements Pipe<IN,OUT> {
    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {

    }

    @Override
    public void init(PipeContext pipeContext) {

    }

    @Override
    public void process(IN input) throws InterruptedException {

    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {

    }
}
