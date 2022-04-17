package com.example.crm.service;

import com.example.crm.model.Manager;

import java.util.List;
import java.util.Optional;

public interface DbServiceManager {
    Manager saveManager(Manager manager);

    Optional<Manager> getManager(long id);

    List<Manager> findAll();

    void deleteManager(long id);
}
