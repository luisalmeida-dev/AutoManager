package org.workshop.automanager.dto.response;

public class CarResponseDTO {
    private Integer id;
    private ModelResponseDTO modelResponse;
    private CustomerResponseDTO customerResponse;
    private String plate;
    private Integer manufactureYear;
    private String color;

    public CarResponseDTO() {
    }

    public CarResponseDTO(Integer id, ModelResponseDTO modelResponse, CustomerResponseDTO customerResponse, String plate, Integer manufactureYear, String color) {
        this.id = id;
        this.modelResponse = modelResponse;
        this.customerResponse = customerResponse;
        this.plate = plate;
        this.manufactureYear = manufactureYear;
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModelResponseDTO getModelResponse() {
        return modelResponse;
    }

    public void setModelResponse(ModelResponseDTO modelResponse) {
        this.modelResponse = modelResponse;
    }

    public CustomerResponseDTO getCustomerResponse() {
        return customerResponse;
    }

    public void setCustomerResponse(CustomerResponseDTO customerResponse) {
        this.customerResponse = customerResponse;
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
