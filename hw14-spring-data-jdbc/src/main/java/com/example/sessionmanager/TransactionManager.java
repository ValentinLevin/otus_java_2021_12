package com.example.sessionmanager;

public interface TransactionManager {
    <T> T doInTransaction(TransactionAction<T> action);
}
