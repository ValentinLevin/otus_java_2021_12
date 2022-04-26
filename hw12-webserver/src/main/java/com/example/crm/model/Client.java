package com.example.crm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "client"
    )
    private List<Phone> phones;

    public Client(String name) {
        this(null, name, null, null);
    }

    public Client(Long id, String name) {
        this(id, name, null, null);
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones == null ? new ArrayList<>() : phones;

        this.phones.forEach(item -> item.setClient(this));
    }

    @Override
    public Client clone() {
        return new Client(
                this.id,
                this.name,
                this.address == null ? null : this.address.clone(),
                this.phones == null ? null : this.phones.stream().map(Phone::clone).toList());
    }

    @Override
    public String toString() {
        return "Client { "
                + " id: " + id
                + ", name: " + name
                + "}";
    }
}
