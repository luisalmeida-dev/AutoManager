package org.workshop.automanager.dto.request;

public class UpdatePasswordRequestDTO {
    private Integer id;
    private String oldPassword;
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
