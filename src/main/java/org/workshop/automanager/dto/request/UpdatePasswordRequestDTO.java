package org.workshop.automanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para alteração de senha de um usuário")
public class UpdatePasswordRequestDTO {
    @Schema(description = "ID do usuário", example = "1")
    private Integer id;

    @Schema(description = "Senha atual", example = "senha123")
    private String oldPassword;

    @Schema(description = "Nova senha", example = "novaSenha456")
    private String newPassword;

    public UpdatePasswordRequestDTO() {
    }

    public UpdatePasswordRequestDTO(Integer id, String oldPassword, String newPassword) {
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
