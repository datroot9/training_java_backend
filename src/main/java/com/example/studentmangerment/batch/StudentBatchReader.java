package com.example.studentmangerment.batch;

import com.example.studentmangerment.dao.StudentDao;
import com.example.studentmangerment.entity.StudentWithInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
@StepScope
@RequiredArgsConstructor
public class StudentBatchReader implements ItemReader<StudentWithInfo> {

    private final StudentDao studentDao;

    // How many records to pull from MySQL in a single database trip
    private final int PAGE_SIZE = 10;
    private int currentOffset = 0;

    // Temporary memory storage for the current page
    private final Queue<StudentWithInfo> studentCache = new LinkedList<>();

    @Override
    public StudentWithInfo read() {
        // If our temporary cache is completely empty, it's time to query the database!
        if (studentCache.isEmpty()) {
            List<StudentWithInfo> nextPage = studentDao.findAllWithPaging(
                    null, null, null, PAGE_SIZE, currentOffset, "s.student_id ASC");

            // Add the new fresh chunk to the queue
            studentCache.addAll(nextPage);

            // Increment the offset so the NEXT trip to the DB gets the following page
            currentOffset += PAGE_SIZE;
        }

        /*
         * Queue.poll() pops the first element out and returns it.
         * If the queue is STILL empty (meaning the database returned absolutely 0 new
         * records),
         * poll() will return `null`. This formally tells Spring Batch
         * "we have reached the end of the data".
         */
        return studentCache.poll();
    }
}
