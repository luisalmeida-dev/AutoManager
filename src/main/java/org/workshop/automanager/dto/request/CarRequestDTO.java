package org.workshop.automanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para cadastrar um veículo")
public class CarRequestDTO {
    @NotBlank
    @NotNull
    @Schema(description = "ID do modelo", example = "1")
    private Integer modelId;

    @NotBlank
    @NotNull
    @Schema(description = "ID do cliente proprietário", example = "1")
    private Integer customerId;

    @NotBlank
    @Schema(description = "Placa do veículo", example = "ABC1D23")
    private String plate;

    @NotNull
    @Schema(description = "Ano de fabricação", example = "2020")
    private Integer manufactureYear;

    @NotBlank
    @Schema(description = "Cor do veículo", example = "Prata")
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
