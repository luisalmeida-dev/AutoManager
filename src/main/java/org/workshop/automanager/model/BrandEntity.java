package org.workshop.automanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "brands")
public class BrandEntity {
    @Id
    @SequenceGenerator(name = "brands_id_seq", sequenceName = "brands_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brands_id_seq")
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



