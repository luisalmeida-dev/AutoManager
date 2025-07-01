package org.workshop.automanager.dto.response;

import org.workshop.automanager.enums.RoleEnum;

public class UserResponseDTO {
    private int id;
    private String name;
    private String login;
    private String password;
    private RoleEnum role;

    public UserResponseDTO(int id, String name, String login, String password, RoleEnum role) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public UserResponseDTO() {
    }

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
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}
