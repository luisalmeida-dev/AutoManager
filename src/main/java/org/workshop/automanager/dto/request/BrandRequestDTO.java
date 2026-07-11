package org.workshop.automanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para criar ou atualizar uma marca")
public class BrandRequestDTO {
    @NotNull(message = "O campo nome não pode ser nulo.")
    @NotBlank(message = "O campo nome não pode ser vazio.")
    @Schema(description = "Nome da marca", example = "Toyota")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
