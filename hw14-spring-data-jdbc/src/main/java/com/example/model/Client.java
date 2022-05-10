package com.example.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Table("client")
public class Client implements Cloneable {
    @Id
    @Column("id")
    private final long id;

    @NonNull
    @Column("name")
    private final String name;

    @Column("client_id")
    private final Address address;

    @MappedCollection(idColumn = "client_id")
    private final Set<Phone> phones;

    @PersistenceConstructor
    public Client(long id, String name, Address address, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones == null ? new HashSet<>() : phones;
    }

    @Override
    public Client clone() {
        return new Client(
                this.id,
                this.name,
                this.address == null ? null : this.address.clone(),
                this.phones == null ? null : this.phones.stream().map(Phone::clone).collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return "Client { "
                + " id: " + id
                + ", name: " + name
                + "}";
    }
}
