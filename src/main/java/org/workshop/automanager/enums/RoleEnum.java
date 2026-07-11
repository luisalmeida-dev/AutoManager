package org.workshop.automanager.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Papéis de usuário/cliente no sistema", enumAsRef = true)
public enum RoleEnum {
    MANAGER("Gestor"), PAINTER("Pintor"), TINSMITH("Funileiro"), CUSTOMER("Cliente");

    private final String role_ptbr;

    RoleEnum(String role_ptbr) {
        this.role_ptbr = role_ptbr;
    }

    public String getRole_ptbr() {
        return role_ptbr;
    }
}
