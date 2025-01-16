package org.workshop.automanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BrandRequestDTO {
    @NotNull(message = "O campo nome não pode ser nulo.")
    @NotBlank(message = "O campo nome não pode ser vazio.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
