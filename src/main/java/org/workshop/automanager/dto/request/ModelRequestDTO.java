package org.workshop.automanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para criar ou atualizar um modelo")
public class ModelRequestDTO {
    @NotNull(message = "O campo nome não pode ser nulo.")
    @Schema(description = "Nome do modelo", example = "Corolla")
    private String name;

    @NotNull(message = "O campo brandId não pode ser nulo.")
    @Schema(description = "ID da marca vinculada", example = "1")
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

    public ModelRequestDTO(String name, Integer brandId) {
        this.name = name;
        this.brandId = brandId;
    }

    public ModelRequestDTO() {
    }
}
