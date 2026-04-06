package com.example.studentmangerment.batch;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.example.studentmangerment.dto.StudentCsvDto;
import com.example.studentmangerment.entity.StudentWithInfo;
import static org.assertj.core.api.Assertions.assertThat;

public class StudentBatchProcessorTest {
    private final StudentBatchProcessor processor = new StudentBatchProcessor();

    @Test
    void testProcess_Corretly() {
        // Arrange
        StudentWithInfo input = new StudentWithInfo();
        input.setId(1);
        input.setCode("STU001");
        input.setName("Dat");
        input.setAddress("Hanoi");
        input.setAverageScore(8.5);
        input.setBirthday(new Date(1704067200000L)); // Jan 1, 2024 (UTC)

        // Act
        StudentCsvDto result = processor.process(input);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCode()).isEqualTo("STU001");
        // Verify the date was formatted correctly based on your DATE_FORMATTER
        assertThat(result.getBirthday()).isEqualTo("2024/01/01");
    }

    @Test
    void testProcess_WithNullBirthday_ReturnsEmptyString() {
        // Arrange
        StudentWithInfo input = new StudentWithInfo();
        input.setId(2);
        input.setBirthday(null); // Explicitly null

        // Act
        StudentCsvDto result = processor.process(input);

        // Assert
        assertThat(result).isNotNull();
        // Verifies the processor gracefully dodges a NullPointerException and falls back to ""
        assertThat(result.getBirthday()).isEqualTo("");
    }

    @Test
    void testProcess_WithNullInput_ThrowsNullPointerException() {
        // Arrange
        StudentWithInfo input = null;

        // Act & Assert
        // We assert that if the reader somehow passes a completely null object, 
        // the processor crashes instead of silently masking it.
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            processor.process(input);
        });
    }
}
