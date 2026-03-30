package com.example.studentmangerment.batch;

import com.example.studentmangerment.dto.StudentCsvDto;
import com.example.studentmangerment.entity.StudentWithInfo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class StudentBatchProcessor implements ItemProcessor<StudentWithInfo, StudentCsvDto> {
    
    // We instantiate the formatter once to be reused across all read chunks
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public StudentCsvDto process(StudentWithInfo studentWithInfo) {
        String formattedDate = "";
        
        // Transform Date to String
        if (studentWithInfo.getBirthday() != null) {
            formattedDate = dateFormat.format(studentWithInfo.getBirthday());
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
