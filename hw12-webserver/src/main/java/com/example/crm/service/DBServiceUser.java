package com.example.crm.service;

import com.example.crm.model.User;

import java.util.Optional;

public interface DBServiceUser {
    User saveUser(User user);
    Optional<User> getByUsername(String username);
}
