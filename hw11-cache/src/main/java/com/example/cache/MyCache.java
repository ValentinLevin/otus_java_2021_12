package com.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private static final int DEFAULT_CAPACITY = 100;
    private final WeakHashMap<K, V> map;
    private final List<WeakReference<HwListener<K, V>>> listeners;

    public MyCache() {
        this(DEFAULT_CAPACITY);
    }

    public MyCache(int capacity) {
        this.map = new WeakHashMap<>(capacity);
        this.listeners = new ArrayList<>();
    }

    @Override
    public void put(K key, V value) {
        this.map.put(key, value);
        notify(key, value, CACHE_ACTION.SAVE);
    }

    @Override
    public void remove(K key) {
        V valueToRemove = map.remove(key);
        if (valueToRemove != null) {
            notify(key, valueToRemove, CACHE_ACTION.REMOVE);
        }
    }

    @Override
    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            notify(key, value, CACHE_ACTION.READ);
        }
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        this.listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        this.listeners.removeIf(listenerRef -> Objects.equals(listenerRef.get(), listener));
    }

    private void notify(K key, V value, CACHE_ACTION action) {
        this.listeners.removeIf(ref -> ref.get() == null);
        this.listeners.forEach(listenerRef -> {
            HwListener<K, V> listener = listenerRef.get();
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
