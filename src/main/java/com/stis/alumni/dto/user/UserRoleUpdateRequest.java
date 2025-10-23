package com.stis.alumni.dto.user;

import com.stis.alumni.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public class UserRoleUpdateRequest {

    @NotNull
    private UserRole role;

    public UserRoleUpdateRequest() {
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
