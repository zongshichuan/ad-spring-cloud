package com.chuan.ad.index;

/**
 * 索引操作接口
 *
 * @param <K>
 * @param <V>
 */
public interface IndexAware<K,V> {

    V get(K key);

    void add(K key,V value);

    void update(K key,V value);

    void delete(K key,V value);
}
