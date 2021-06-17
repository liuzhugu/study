package org.liuzhugu.javastudy.course.ruyuanconcurrent.twostagetermination;

public class TerminationToken {
    private boolean shutdowm;

    public boolean reserva;

    public TerminationToken(boolean shutdowm) {
        this.shutdowm = shutdowm;
    }


    public boolean isToShutdown() {
        return shutdowm;
    }

    public void shutdown() {
        shutdowm = true;
    }

    public void notifyThreadTermination(Thread thread) {

    }
}
