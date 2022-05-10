package com.example.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Getter
@Table("phone")
public class Phone implements Cloneable {
    @Id
    @Column("id")
    private final long id;

    @NonNull
    @Column("number")
    private final String number;

    @PersistenceConstructor
    public Phone(long id, String number) {
        this.id = id;
        this.number = number;
    }

    public Phone(String number) {
        this(0, number);
    }

    @Override
    public Phone clone() {
        return new Phone(this.id, this.number);
    }

    @Override
    public String toString() {
        return "Phone { "
                + "id: " + id
                + ", number: " + number
                + " }";
    }
}
