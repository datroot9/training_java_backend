package com.example.studentmangerment.dao;

import com.example.studentmangerment.entity.StudentInfo;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Optional;

/**
 * Doma DAO for {@link StudentInfo} persistence operations.
 */
@Dao
@ConfigAutowireable
public interface StudentInfoDao {
    /**
     * Inserts a new student info row.
     *
     * @param studentInfo entity to insert
     * @return Doma result containing the persisted entity (including generated id)
     */
    @Insert
    Result<StudentInfo> insert(StudentInfo studentInfo);

    /**
     * Finds info by owning student id.
     *
     * @param studentId foreign key to {@code student.student_id}
     * @return info row if it exists
     */
    @Select
    Optional<StudentInfo> findByStudentId(Integer studentId);

    /**
     * Updates an existing student info row.
     *
     * @param studentInfo entity with primary key set
     * @return Doma update result
     */
    @Update
    Result<StudentInfo> update(StudentInfo studentInfo);
}
