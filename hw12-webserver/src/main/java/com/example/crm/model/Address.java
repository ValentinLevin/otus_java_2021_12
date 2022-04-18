package com.example.crm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "address")
public class Address implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "street", nullable = false)
    private String street;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "address")
    private Client client;

    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }

    public Address(String street) {
        this.street = street;
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
