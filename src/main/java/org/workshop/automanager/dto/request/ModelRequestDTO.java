package org.workshop.automanager.dto.request;

import jakarta.validation.constraints.NotNull;

public class ModelRequestDTO {
    @NotNull(message = "O campo nome não pode ser nulo.")
    private String name;
    @NotNull(message = "O campo brandId não pode ser nulo.")
    private Integer brandId;

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