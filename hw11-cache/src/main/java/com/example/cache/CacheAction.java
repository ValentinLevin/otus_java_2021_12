package com.example.cache;

public enum CacheAction {
    READ("read"),
    SAVE("save"),
    REMOVE("remove");

    private final String name;

    public String getName() {
        return this.name;
    }

    CacheAction(String name) {
        this.name = name;
    }
}
