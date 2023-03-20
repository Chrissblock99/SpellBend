package me.chriss99.spellbend.util;

@FunctionalInterface
public interface  TriFunction<K, V, S, R> {
    R apply(K arg1, V arg2, S arg3);
}
