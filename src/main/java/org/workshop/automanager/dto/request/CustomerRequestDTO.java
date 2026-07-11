package org.workshop.automanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.workshop.automanager.enums.RoleEnum;

@Schema(description = "Dados para criar ou atualizar um cliente")
public class CustomerRequestDTO {
    @Schema(description = "Nome completo", example = "João Silva")
    private String name;

    @Schema(description = "CPF (somente números)", example = "12345678901")
    private String cpf;

    @Schema(description = "E-mail", example = "joao.silva@email.com")
    private String email;

    @Schema(description = "Telefone", example = "11999998888")
    private String phone;

    @Schema(description = "Endereço", example = "Rua das Flores, 100")
    private String address;

    @Schema(description = "Papel do cliente no sistema", example = "CUSTOMER")
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
