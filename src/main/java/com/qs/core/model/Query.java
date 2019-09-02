package com.qs.core.model;

public class Query<K, P, V> {
    public final K key;
    public final P path;
    public final V value;

    public Query(K key, P path, V value) {
        this.key = key;
        this.path = path;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Query)) {
            return false;
        }
        Query<?, ?, ?> p = (Query<?, ?, ?>) o;
        return equalsObject(p.key, key) && equalsObject(p.path, path) && equalsObject(p.value, value);
    }

    private boolean equalsObject(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (path == null ? 0 : path.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return "Query{" + key + " " + path + " " + value + "}";
    }

    public static <K, P, V> Query<K, P, V> create(K key, P path, V value) {
        return new Query<K, P, V>(key, path, value);
    }
}
