package com.stis.alumni.dto.user;

import jakarta.validation.constraints.NotBlank;

public class AccountDeletionRequest {

    @NotBlank
    private String password;

    @NotBlank
    private String confirmation;

    public AccountDeletionRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
}
