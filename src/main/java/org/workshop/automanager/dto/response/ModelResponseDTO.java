package org.workshop.automanager.dto.response;

public class ModelResponseDTO {
    private int id;
    private String name;
    private String brandName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public ModelResponseDTO(int id, String name, String brandName) {
        this.id = id;
        this.name = name;
        this.brandName = brandName;
    }
}
