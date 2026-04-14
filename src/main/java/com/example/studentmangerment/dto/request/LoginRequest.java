package com.example.studentmangerment.dto.request;

import com.example.studentmangerment.validation.AsciiOnly;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Credentials payload for {@code POST /api/auth/login}.
 */
@Data
public class LoginRequest {
    /** Registered username (validated as email format). */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 20, message = "Email must be less than 20 characters")
    @AsciiOnly(message = "Username can only contain English letters, numbers, and basic symbols")
    private String username;

    /** Plain-text password for verification. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be at least 6 characters and no more than 15 characters")
    @AsciiOnly(message = "Password can only contain English letters, numbers, and basic symbols")
    private String password;
}
