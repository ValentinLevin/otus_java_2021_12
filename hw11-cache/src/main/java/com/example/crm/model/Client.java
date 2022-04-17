package com.example.crm.model;


import com.example.core.annotations.Id;

import java.util.Objects;

public class Client {
    @Id
    private Long id;
    private String name;

    public Client() {}

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client { "
                + "id = " + id
                + ", name = " + name
                + " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Client client = (Client) obj;

        return (Objects.equals(client.getId(), this.getId()))
                && (Objects.equals(client.getName(), this.getName()));
    }
}
