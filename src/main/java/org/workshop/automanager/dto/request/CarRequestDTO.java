package org.workshop.automanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CarRequestDTO {
    @NotBlank
    @NotNull
    private Integer modelId;
    @NotBlank
    @NotNull
    private Integer customerId;
    @NotBlank
    private String plate;
    @NotNull
    private Integer manufactureYear;
    @NotBlank
    private String color;

    public CarRequestDTO() {
    }

    public CarRequestDTO(Integer modelId, Integer customerId, String plate, Integer manufactureYear, String color) {
        this.modelId = modelId;
        this.customerId = customerId;
        this.plate = plate;
        this.manufactureYear = manufactureYear;
        this.color = color;
    }


    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
