package com.qs.core.model;

import java.util.Objects;

public class Pair<K, V> {
    public final K key;
    public final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return equalsObject(p.key, key) && equalsObject(p.value, value);
    }

    private boolean equalsObject(Object a, Object b) {
        return Objects.equals(a, b);
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return "Pair{" + key + " " + value + "}";
    }

    public static <K, V> Pair<K, V> create(K key, V value) {
        return new Pair<K, V>(key, value);
    }
}
