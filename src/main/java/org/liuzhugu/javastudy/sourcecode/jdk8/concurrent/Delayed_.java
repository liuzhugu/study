package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

import java.util.concurrent.TimeUnit;

public interface Delayed_ extends Comparable<Delayed_> {

    /**
     * Returns the remaining delay associated with this object, in the
     * given time unit.
     *
     * @param unit the time unit
     * @return the remaining delay; zero or negative values indicate
     * that the delay has already elapsed
     */
    long getDelay(TimeUnit unit);
}

