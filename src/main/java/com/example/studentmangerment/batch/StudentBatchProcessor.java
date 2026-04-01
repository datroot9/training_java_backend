package com.example.studentmangerment.batch;

import com.example.studentmangerment.dto.StudentCsvDto;
import com.example.studentmangerment.entity.StudentWithInfo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class StudentBatchProcessor implements ItemProcessor<StudentWithInfo, StudentCsvDto> {
    
    // We instantiate the formatter once to be reused across all read chunks
    // DateTimeFormatter is immutable and thread-safe
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            .withZone(ZoneId.systemDefault());

    @Override
    public StudentCsvDto process(StudentWithInfo studentWithInfo) {
        String formattedDate = "";
        
        // Transform Date to String using thread-safe formatter
        if (studentWithInfo.getBirthday() != null) {
            formattedDate = DATE_FORMATTER.format(studentWithInfo.getBirthday().toInstant());
        }

        // Return the clean, formatted DTO
        return StudentCsvDto.builder()
                .id(studentWithInfo.getId())
                .code(studentWithInfo.getCode())
                .name(studentWithInfo.getName())
                .address(studentWithInfo.getAddress())
                .averageScore(studentWithInfo.getAverageScore())
                .birthday(formattedDate)
                .build();
    }
}
