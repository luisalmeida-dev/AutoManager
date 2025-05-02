package org.workshop.automanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.workshop.automanager.enums.RoleEnum;

public class UserRequestDTO {
    @NotNull(message = "O campo [NAME] não pode ser nulo.")
    @NotBlank(message = "O campo [NAME] não pode ser vazio.")
    private String name;

    @NotNull(message = "O campo [LOGIN] não pode ser nulo.")
    @NotBlank(message = "O campo [LOGIN] não pode ser vazio.")
    private String login;
    @NotNull(message = "O campo [PASSWORD] não pode ser nulo.")
    @NotBlank(message = "O campo [PASSWORD] não pode ser vazio.")
    private String password;

    @NotNull(message = "O campo [ROLE] não pode ser nulo.")
    @NotBlank(message = "O campo [ROLE] não pode ser vazio.")
    private RoleEnum Role;

    public UserRequestDTO() {
    }

    public UserRequestDTO(String name, String login, String password, RoleEnum role) {
        this.name = name;
        this.login = login;
        this.password = password;
        Role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleEnum getRole() {
        return Role;
    }

    public void setRole(RoleEnum role) {
        Role = role;
    }
}
