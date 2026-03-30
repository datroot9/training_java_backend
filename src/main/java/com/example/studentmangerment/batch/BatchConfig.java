package com.example.studentmangerment.batch;

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

import com.example.studentmangerment.entity.StudentWithInfo;

@Configuration
public class BatchConfig {
    @Bean
    public FlatFileItemWriter<StudentWithInfo> studentCsvWriter() {
        return new FlatFileItemWriterBuilder<StudentWithInfo>()
                .name("studentCsvWriter")
                .resource(new FileSystemResource("student.csv")) // Output file
                .delimited() // Comma separated by default
                /*
                 * The names here MUST EXACTLY map to the variable names inside your
                 * StudentWithInfo class
                 * e.g., if you have `private String name;`, put "name" here.
                 */
                .names("id", "code", "name", "address", "averageScore", "birthday")
                .headerCallback(writer -> writer.write("ID,Student Code,Student Name,Address,Average Score,Birthday"))
                .build();
    }

    @Bean
    public Job exportStudentsJob(JobRepository jobRepository, Step exportStudentsStep) {
        return new JobBuilder("exportStudentsJob", jobRepository)
                .start(exportStudentsStep)
                .build();
    }

    @Bean
    public Step exportStudentsStep(JobRepository jobRepository, StudentBatchReader reader,
            FlatFileItemWriter<StudentWithInfo> writer,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("exportStudentsStep", jobRepository)
                .<StudentWithInfo, StudentWithInfo>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
