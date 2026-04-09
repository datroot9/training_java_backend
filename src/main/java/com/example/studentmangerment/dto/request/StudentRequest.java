package com.example.studentmangerment.dto.request;

import com.example.studentmangerment.validation.ByteSize;
import com.example.studentmangerment.validation.StudentCode;
import com.example.studentmangerment.validation.ValidBirthday;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @ByteSize(max = 20, message = "Name must not exceed 20 bytes")
    private String name;

    @NotBlank(message = "Code is required")
    @StudentCode
    @ByteSize(max = 10, message = "Code must not exceed 10 bytes")
    private String code;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must not exceed 255 bytes")
    @ByteSize(max = 255, message = "Address must not exceed 255 bytes")
    private String address;

    @NotNull(message = "Average score is required")
    @DecimalMin(value = "0.0", message = "Average score must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Average score must be at most 10.0")
    private Double averageScore;

    @NotNull(message = "Birthday is required")
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "UTC")
    @ValidBirthday
    private Date birthday;
}
