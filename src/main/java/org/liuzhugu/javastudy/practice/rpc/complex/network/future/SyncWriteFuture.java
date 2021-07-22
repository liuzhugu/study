package org.liuzhugu.javastudy.practice.rpc.complex.network.future;

import org.liuzhugu.javastudy.practice.rpc.complex.network.msg.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncWriteFuture  implements WriteFuture<Response> {
    private CountDownLatch latch = new CountDownLatch(1);
    private final long begin = System.currentTimeMillis();
    private final String requestId;
    private Response response;
    private Throwable cause;
    private long timeout;
    private boolean writeResult;
    private boolean isTimeout;

    public SyncWriteFuture(String requestId) {
        this.requestId = requestId;
    }

    public SyncWriteFuture(String requestId,long timeout) {
        this.requestId = requestId;
        this.timeout = timeout;
        this.isTimeout = false;
        this.writeResult = true;
    }

    @Override
    public Throwable cause() {
        return this.cause;
    }

    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public boolean isWriteSuccess() {
        return this.writeResult;
    }

    @Override
    public void setWriteResult(boolean result) {
        this.writeResult = result;
    }

    @Override
    public String requestId() {
        return this.requestId;
    }

    @Override
    public Response response() {
        return this.response;
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
        latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        //等待结果返回
        latch.await();
        return response;
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout,unit)) {
            return response;
        }
        return null;
    }

    @Override
    public boolean isTimeOut() {
        if (isTimeout) {
            return isTimeout;
        }
        return System.currentTimeMillis() - begin > timeout;
    }
}
