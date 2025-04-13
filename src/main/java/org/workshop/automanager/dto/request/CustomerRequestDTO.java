package org.workshop.automanager.dto.request;

import org.workshop.automanager.enums.RoleEnum;

public class CustomerRequestDTO {
    private String name;

    private String cpf;

    private String email;

    private String phone;

    private String address;

    private RoleEnum role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public CustomerRequestDTO(String name, String cpf, String email, String phone, String address, RoleEnum role) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public CustomerRequestDTO() {
    }
}
