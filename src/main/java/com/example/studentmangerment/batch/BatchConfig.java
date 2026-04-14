package com.example.studentmangerment.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.studentmangerment.dto.StudentCsvDto;
import com.example.studentmangerment.entity.StudentWithInfo;

/**
 * Spring Batch job that exports all students to a semicolon-delimited CSV file.
 */
@Configuration
public class BatchConfig {
    /**
     * Writes CSV rows to {@code student_{timestamp}.csv} using job parameter {@code timestamp}.
     *
     * @param timestamp job parameter for unique output filename
     * @return configured flat file writer
     */
    @Bean
    @StepScope
    public FlatFileItemWriter<StudentCsvDto> studentCsvWriter(
            @Value("#{jobParameters['timestamp']}") Long timestamp) {

        long time = timestamp != null ? timestamp : System.currentTimeMillis();
        String filename = "student_" + time + ".csv";

        return new FlatFileItemWriterBuilder<StudentCsvDto>()
                .name("studentCsvWriter")
                .resource(new FileSystemResource(filename)) // Dynamic output file
                .delimited().delimiter(";") // Comma separated by default
                .names("id", "code", "name", "birthday", "address", "averageScore")
                .headerCallback(writer -> writer.write("ID;Student Code;Student Name;Birthday;Address;Average Score"))
                .build();
    }

    /**
     * Top-level export job with a single step.
     *
     * @param jobRepository batch job repository
     * @param exportStudentsStep read-process-write step
     * @return job definition
     */
    @Bean
    public Job exportStudentsJob(JobRepository jobRepository, Step exportStudentsStep) {
        return new JobBuilder("exportStudentsJob", jobRepository)
                .start(exportStudentsStep)
                .build();
    }

    /**
     * Chunk-oriented step: read {@link StudentWithInfo}, map to {@link StudentCsvDto}, write CSV.
     *
     * @param jobRepository batch job repository
     * @param reader paging reader over students
     * @param processor entity to CSV row mapper
     * @param writer CSV file writer
     * @param transactionManager transaction manager for chunk boundaries
     * @return configured step
     */
    @Bean
    public Step exportStudentsStep(JobRepository jobRepository, StudentBatchReader reader,
            StudentBatchProcessor processor,
            FlatFileItemWriter<StudentCsvDto> writer,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("exportStudentsStep", jobRepository)
                .<StudentWithInfo, StudentCsvDto>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
