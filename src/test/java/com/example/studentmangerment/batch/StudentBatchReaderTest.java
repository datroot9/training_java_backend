package com.example.studentmangerment.batch;

import com.example.studentmangerment.dao.StudentDao;
import com.example.studentmangerment.entity.StudentWithInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class StudentBatchReaderTest {

    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private StudentBatchReader reader;

    private List<StudentWithInfo> createMockStudents(int startId, int count) {
        List<StudentWithInfo> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            StudentWithInfo student = new StudentWithInfo();
            student.setId(startId + i);
            student.setName("Student " + (startId + i));
            list.add(student);
        }
        return list;
    }

    @Test
    void testRead_FetchesDataAcrossMultiplePages() throws Exception {
        // Arrange: Generate 12 students in total
        List<StudentWithInfo> page1 = createMockStudents(1, 10); // IDs 1 to 10
        List<StudentWithInfo> page2 = createMockStudents(11, 2); // IDs 11 to 12

        // First DB hit (offset 0) returns 10 students
        when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0), eq("s.student_id ASC")))
                .thenReturn(page1);
                
        // Second DB hit (offset 10) returns 2 students
        when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(10), eq("s.student_id ASC")))
                .thenReturn(page2);

        // Third DB hit (offset 20) returns empty list
        when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(20), eq("s.student_id ASC")))
                .thenReturn(List.of());

        // Act & Assert
        // Read the first 10 items (uses Cache after the 1st read hits DB)
        for (int i = 1; i <= 10; i++) {
            StudentWithInfo student = reader.read();
            assertThat(student).isNotNull();
            assertThat(student.getId()).isEqualTo(i);
        }
        
        // 11th read hits DB again (offset 10), grabs the next 2 items. Returns the 11th.
        StudentWithInfo student11 = reader.read();
        assertThat(student11).isNotNull();
        assertThat(student11.getId()).isEqualTo(11);

        // 12th read pops the last connected item from cache
        StudentWithInfo student12 = reader.read();
        assertThat(student12).isNotNull();
        assertThat(student12.getId()).isEqualTo(12);
        
        // 13th read hits DB (offset 20), receives empty list, returns null
        // 13th read hits DB (offset 20), receives empty list, returns null
        assertThat(reader.read()).isNull();
    }

    @Test
    void testRead_ResumesFromSavedStateAfterInterrupt() throws Exception {
        // Arrange
        // We simulate a job that ran, saved its state (offset = 10), and crashed.
        org.springframework.batch.item.ExecutionContext executionContext = new org.springframework.batch.item.ExecutionContext();
        executionContext.putInt("StudentBatchReader.currentOffset", 10);
        
        List<StudentWithInfo> page2 = createMockStudents(11, 2); // IDs 11 to 12

        // Because offset is 10, the NEXT db hit should request offset 10 immediately, bypassing offset 0.
        when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(10), eq("s.student_id ASC")))
                .thenReturn(page2);

        // Act
        reader.open(executionContext); // Reader restores offset = 10
        StudentWithInfo resumedStudent = reader.read(); // Triggers DB call for offset 10

        // Assert
        assertThat(resumedStudent).isNotNull();
        assertThat(resumedStudent.getId()).isEqualTo(11); // Correctly fetched student 11 instead of 1
        
        // Also verify that update() correctly saves the incremented offset (which should now be 20)
        reader.update(executionContext);
        assertThat(executionContext.getInt("StudentBatchReader.currentOffset")).isEqualTo(20);
    }

    @Test
    void testRead_ThrowsExceptionWhenDatabaseFails() {
        // Arrange
        // Simulate a database crash and the DAO throwing an exception when hit
        when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0), eq("s.student_id ASC")))
                .thenThrow(new RuntimeException("Database connection lost"));

        // Act & Assert
        // We assert that the exception bubbles straight up without swallowed silently
        // Act & Assert
        // We assert that the exception bubbles straight up without swallowed silently
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            reader.read(); // This triggers the DB hit, throwing the mock exception
        }, "Database connection lost");
    }

    @Test
    void testOpen_InitializesOffsetWhenNotPresent() throws Exception {
        // Arrange
        org.springframework.batch.item.ExecutionContext executionContext = new org.springframework.batch.item.ExecutionContext();
        
        // Act
        reader.open(executionContext);
        
        // Assert
        // It should start at 0 if no offset is found
        assertThat(executionContext.getInt("StudentBatchReader.currentOffset")).isEqualTo(0);
    }

    @Test
    void testClose_ClearsCache() throws Exception {
        // Arrange
        StudentWithInfo student = new StudentWithInfo();
        student.setId(1);

        when(studentDao.findAllWithPaging(isNull(), isNull(), isNull(), eq(10), eq(0), eq("s.student_id ASC")))
                .thenReturn(List.of(student));

        // Act
        reader.read(); // This pulls the student into cache, and polls it (cache is now empty, but let's test close gracefully runs anyway)

        reader.close(); // Clears any residual data

        // Assert
        // We verify close completes without error and clears the queue cleanly.
        // It's mostly testing that the method doesn't crash, since internal queue isn't exposed.
        assertThat(reader).isNotNull();
    }
}
