package org.workshop.automanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "model_tb")
public class ModelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "model_tb_seq")
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BrandEntity getBrand() {
        return brand;
    }

    public void setBrand(BrandEntity brand) {
        this.brand = brand;
    }
}
