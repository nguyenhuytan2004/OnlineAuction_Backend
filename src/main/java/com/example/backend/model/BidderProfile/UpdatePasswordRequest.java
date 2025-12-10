package com.example.backend.model.BidderProfile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    @NotNull(message = "Current password is required")
    @Size(min = 6, message = "Current password must be at least 8 characters long")
    @Size(max = 50, message = "Current password must not exceed 50 characters")
    private String currentPassword;

    @NotNull(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 8 characters long")
    @Size(max = 50, message = "New password must not exceed 50 characters")
    private String newPassword;

    @NotNull(message = "Confirm new password is required")
    @Size(min = 6, message = "Confirm new password must be at least 8 characters long")
    @Size(max = 50, message = "Confirm new password must not exceed 50 characters")
    private String confirmPassword;
}
