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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "service_orders")
public class ServiceOrderEntity {

    @Id
    @SequenceGenerator(name = "service_orders_id_seq", sequenceName = "service_orders_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_orders_id_seq")
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workshop_id", nullable = false)
    private WorkshopEntity workshop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by_employee_id")
    private EmployeeEntity openedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by_employee_id")
    private EmployeeEntity evaluatedBy;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "evaluation_notes")
    private String evaluationNotes;

    @Column(name = "evaluated_at")
    private OffsetDateTime evaluatedAt;

    @Column(name = "snapshot_customer_name", nullable = false, length = 100)
    private String snapshotCustomerName;

    @Column(name = "snapshot_customer_phone", length = 20)
    private String snapshotCustomerPhone;

    @Column(name = "snapshot_customer_email", length = 100)
    private String snapshotCustomerEmail;

    @Column(name = "snapshot_customer_cpf", length = 11)
    private String snapshotCustomerCpf;

    @Column(name = "snapshot_vehicle_plate", nullable = false, length = 10)
    private String snapshotVehiclePlate;

    @Column(name = "snapshot_vehicle_make_model", nullable = false, length = 100)
    private String snapshotVehicleMakeModel;

    @Column(name = "snapshot_vehicle_year")
    private Integer snapshotVehicleYear;

    @Column(name = "snapshot_vehicle_color", length = 30)
    private String snapshotVehicleColor;

    @Column(name = "estimate", precision = 10, scale = 2)
    private BigDecimal estimate;

    @Column(name = "final_value", precision = 10, scale = 2)
    private BigDecimal finalValue;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

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

    public VehicleEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleEntity vehicle) {
        this.vehicle = vehicle;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public EmployeeEntity getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(EmployeeEntity openedBy) {
        this.openedBy = openedBy;
    }

    public EmployeeEntity getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(EmployeeEntity evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvaluationNotes() {
        return evaluationNotes;
    }

    public void setEvaluationNotes(String evaluationNotes) {
        this.evaluationNotes = evaluationNotes;
    }

    public OffsetDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(OffsetDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public String getSnapshotCustomerName() {
        return snapshotCustomerName;
    }

    public void setSnapshotCustomerName(String snapshotCustomerName) {
        this.snapshotCustomerName = snapshotCustomerName;
    }

    public String getSnapshotCustomerPhone() {
        return snapshotCustomerPhone;
    }

    public void setSnapshotCustomerPhone(String snapshotCustomerPhone) {
        this.snapshotCustomerPhone = snapshotCustomerPhone;
    }

    public String getSnapshotCustomerEmail() {
        return snapshotCustomerEmail;
    }

    public void setSnapshotCustomerEmail(String snapshotCustomerEmail) {
        this.snapshotCustomerEmail = snapshotCustomerEmail;
    }

    public String getSnapshotCustomerCpf() {
        return snapshotCustomerCpf;
    }

    public void setSnapshotCustomerCpf(String snapshotCustomerCpf) {
        this.snapshotCustomerCpf = snapshotCustomerCpf;
    }

    public String getSnapshotVehiclePlate() {
        return snapshotVehiclePlate;
    }

    public void setSnapshotVehiclePlate(String snapshotVehiclePlate) {
        this.snapshotVehiclePlate = snapshotVehiclePlate;
    }

    public String getSnapshotVehicleMakeModel() {
        return snapshotVehicleMakeModel;
    }

    public void setSnapshotVehicleMakeModel(String snapshotVehicleMakeModel) {
        this.snapshotVehicleMakeModel = snapshotVehicleMakeModel;
    }

    public Integer getSnapshotVehicleYear() {
        return snapshotVehicleYear;
    }

    public void setSnapshotVehicleYear(Integer snapshotVehicleYear) {
        this.snapshotVehicleYear = snapshotVehicleYear;
    }

    public String getSnapshotVehicleColor() {
        return snapshotVehicleColor;
    }

    public void setSnapshotVehicleColor(String snapshotVehicleColor) {
        this.snapshotVehicleColor = snapshotVehicleColor;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(OffsetDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
