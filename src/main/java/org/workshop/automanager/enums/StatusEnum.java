package org.workshop.automanager.enums;

public enum StatusEnum {
    WAITING_FOR_APPROVAL("Aguardando aprovação"),
    AWAITING_PARTS("Aguardando peças"),
    IN_REPAIR("Em reparo"),
    COMPLETED("Concluída"),
    ON_HOLD("Em espera"),
    BLOCKED("Bloqueado");

    private final String status_ptbr;

    StatusEnum(String status_ptbr) {
        this.status_ptbr = status_ptbr;
    }

    public String getStatus_ptbr() {
        return status_ptbr;
    }
}
