package com.example.studentmangerment.dto;

import com.example.studentmangerment.validation.AsciiOnly;
import lombok.Data;

@Data
public class LoginRequest {
    @AsciiOnly(message = "Username can only contain English letters, numbers, and basic symbols")
    private String username;

    @AsciiOnly(message = "Password can only contain English letters, numbers, and basic symbols")
    private String password;
}
