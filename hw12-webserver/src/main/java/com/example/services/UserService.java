package com.example.services;

public interface UserService {
    boolean isExists(String username);
    void createUser(String username, String password);
    boolean authenticate(String username, String password);
}
