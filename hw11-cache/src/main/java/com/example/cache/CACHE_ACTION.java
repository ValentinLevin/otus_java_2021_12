package com.example.cache;

public enum CACHE_ACTION {
    READ("read"),
    SAVE("save"),
    REMOVE("remove");

    private final String name;

    public String getName() {
        return this.name;
    }

    CACHE_ACTION(String name) {
        this.name = name;
    }
}
