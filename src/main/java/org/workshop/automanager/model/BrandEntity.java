package org.workshop.automanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "brand_tb")
public class BrandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "brand_tb_seq")
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



