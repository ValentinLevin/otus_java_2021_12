package com.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final WeakHashMap<K, V> map;
    private final List<HwListener<K, V>> listeners;

    public MyCache() {
        this.map = new WeakHashMap<>();
        this.listeners = new ArrayList<>();
    }

    @Override
    public void put(K key, V value) {
        this.map.put(key, value);
        notify(key, value, CacheAction.SAVE);
    }

    @Override
    public void remove(K key) {
        V valueToRemove = map.remove(key);
        if (valueToRemove != null) {
            notify(key, valueToRemove, CacheAction.REMOVE);
        }
    }

    @Override
    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            notify(key, value, CacheAction.READ);
        }
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        this.listeners.remove(listener);
    }

    private void notify(K key, V value, CacheAction action) {
        this.listeners.forEach(listener -> {
            if (listener != null) {
                try {
                    listener.notify(key, value, action.getName());
                } catch (Exception e) {
                    logger.error("Ошибка при вызове подписчика: {}, ошибка: {}", listener, e, e);
                }
            }
        });
    }
}
