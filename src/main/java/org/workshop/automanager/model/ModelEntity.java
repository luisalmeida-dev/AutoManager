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

    @JoinColumn(name = "brand_id")
    private Integer brandId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
}
