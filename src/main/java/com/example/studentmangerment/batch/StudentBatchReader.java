package com.example.studentmangerment.batch;

import com.example.studentmangerment.dao.StudentDao;
import com.example.studentmangerment.entity.StudentWithInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Paging {@link ItemStreamReader} that loads students in fixed-size chunks for CSV export.
 */
@Component
@StepScope
@RequiredArgsConstructor
public class StudentBatchReader implements ItemStreamReader<StudentWithInfo> {

    /** Execution context key for restartable offset. */
    private static final String OFFSET_KEY = "StudentBatchReader.currentOffset";

    /** Loads pages of {@link StudentWithInfo} from the database. */
    private final StudentDao studentDao;

    /** Number of rows fetched per database round trip. */
    private final int PAGE_SIZE = 10;
    /** Current SQL offset for the next page fetch. */
    private int currentOffset = 0;

    /** Buffered rows for the current page not yet returned to the chunk processor. */
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

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // When the job starts or restarts, check if we have a saved offset.
        if (executionContext.containsKey(OFFSET_KEY)) {
            // Restore the state from the previous interrupted run
            currentOffset = executionContext.getInt(OFFSET_KEY);
        } else {
            // Fresh run, start from 0
            currentOffset = 0;
            executionContext.putInt(OFFSET_KEY, currentOffset);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // Periodically invoked by Spring Batch (usually at chunk boundaries).
        // Save our current progress in case of failure.
        executionContext.putInt(OFFSET_KEY, currentOffset);
    }

    @Override
    public void close() throws ItemStreamException {
        // Clean up resources if necessary when step completes.
        studentCache.clear();
    }
}
