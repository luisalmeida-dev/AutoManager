package org.workshop.automanager.model;

import jakarta.persistence.*;
import org.workshop.automanager.enums.StatusEnum;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "task_tb")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "task_tb_seq")
    @Column(name = "id")
    private int id;

    @Column(name = "description")
    private String description;

    @Column(name = "observation")
    private String observation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum statusEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "estimate")
    private BigDecimal estimate;

    @Column(name = "final_value")
    private BigDecimal finalValue;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public CarEntity getCar() {
        return car;
    }

    public void setCar(CarEntity car) {
        this.car = car;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getEstimate() {
        return estimate;
    }

    public void setEstimate(BigDecimal estimate) {
        this.estimate = estimate;
    }

    public BigDecimal getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(BigDecimal finalValue) {
        this.finalValue = finalValue;
    }
}
