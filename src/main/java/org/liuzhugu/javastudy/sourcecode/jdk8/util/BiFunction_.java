package org.liuzhugu.javastudy.sourcecode.jdk8.util;

import java.util.Objects;
import java.util.function.Function;

public interface BiFunction_ <T, U, R> {


    R apply(T t, U u);


    default <V> BiFunction_<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> after.apply(apply(t, u));
    }
}

