package com.example.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("address")
public class Address implements Cloneable {
    @Id
    @Column("id")
    private final long id;

    @Column("street")
    private final String street;

    @PersistenceConstructor
    public Address(long id, String street) {
        this.id = id;
        this.street = street;
    }

    public Address(String street) {
        this(0, street);
    }

    @Override
    public Address clone() {
        return new Address(this.id, this.street);
    }

    @Override
    public String toString() {
        return "Address { "
                + "id: " + id
                + ", street: " + street
                + " }";
    }
}
