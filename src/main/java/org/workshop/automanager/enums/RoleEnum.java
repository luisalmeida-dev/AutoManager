package org.workshop.automanager.enums;

public enum RoleEnum {
    MANAGER("Gestor"), PAINTER("Pintor"), TINSMITH("Funileiro");

    private final String role_ptbr;

    RoleEnum(String role_ptbr) {
        this.role_ptbr = role_ptbr;
    }

    public String getRole_ptbr() {
        return role_ptbr;
    }
}
