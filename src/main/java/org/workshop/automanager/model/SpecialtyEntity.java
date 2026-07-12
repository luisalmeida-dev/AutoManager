package org.workshop.automanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "specialties")
public class SpecialtyEntity {

    @Id
    @SequenceGenerator(name = "specialties_id_seq", sequenceName = "specialties_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "specialties_id_seq")
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workshop_id", nullable = false)
    private WorkshopEntity workshop;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WorkshopEntity getWorkshop() {
        return workshop;
    }

    public void setWorkshop(WorkshopEntity workshop) {
        this.workshop = workshop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
