package com.example.studentmangerment.dto.request;

import com.example.studentmangerment.validation.AsciiOnly;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload for {@code POST /api/auth/register}; passwords must match before hashing.
 */
@Data
public class RegisterRequest {
    /** Desired unique username (validated as email format). */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 20, message = "Email must be less than 20 characters")
    @AsciiOnly(message = "Username can only contain English letters, numbers, and basic symbols")
    private String username;

    /** Plain-text password to hash and store. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be at least 6 characters and no more than 15 characters")
    @AsciiOnly(message = "Password can only contain English letters, numbers, and basic symbols")
    private String password;

    /** Must equal {@link #password} for the registration to succeed. */
    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, max = 15, message = "Password must be at least 6 characters and no more than 15 characters")
    @AsciiOnly(message = "Confirm password can only contain English letters, numbers, and basic symbols")
    private String confirmPassword;

}
