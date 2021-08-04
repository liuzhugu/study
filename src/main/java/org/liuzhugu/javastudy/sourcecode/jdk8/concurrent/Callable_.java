package org.liuzhugu.javastudy.sourcecode.jdk8.concurrent;

@FunctionalInterface
public interface Callable_<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    V call() throws Exception;
}
