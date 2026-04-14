package com.example.studentmangerment.dao;

import com.example.studentmangerment.entity.Student;
import com.example.studentmangerment.entity.StudentWithInfo;
import org.seasar.doma.*;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Doma DAO for {@link Student} and joined {@link StudentWithInfo} queries.
 */
@Dao
@ConfigAutowireable
public interface StudentDao {
    /**
     * Inserts a new student row.
     *
     * @param student entity to insert
     * @return Doma result with generated id
     */
    @Insert
    Result<Student> insert(Student student);

    /**
     * Loads a student by primary key.
     *
     * @param id student id
     * @return student if found
     */
    @Select
    Optional<Student> findById(Integer id);

    /**
     * Loads a student with joined info by primary key.
     *
     * @param id student id
     * @return joined row if found
     */
    @Select
    Optional<StudentWithInfo> findStudentWithInfoById(Integer id);

    /**
     * Loads a student by business code.
     *
     * @param code student code
     * @return student if found
     */
    @Select
    Optional<Student> findByCode(String code);

    /**
     * Loads a student with joined info by business code.
     *
     * @param code student code
     * @return joined row if found
     */
    @Select
    Optional<StudentWithInfo> findWithInfoByCode(String code);

    /**
     * Updates an existing student row.
     *
     * @param student entity with id set
     * @return Doma update result
     */
    @Update
    Result<Student> update(Student student);

    /**
     * Deletes a student row.
     *
     * @param student entity to delete (typically loaded first)
     * @return Doma delete result
     */
    @Delete
    Result<Student> delete(Student student);

    /**
     * Lists students with optional filters, paging, and SQL {@code ORDER BY} clause.
     *
     * @param code optional code filter
     * @param name optional name filter
     * @param birthday optional birthday filter
     * @param limit page size
     * @param offset row offset
     * @param orderByClause safe fragment appended to {@code ORDER BY}
     * @return page of joined rows
     */
    @Select
    List<StudentWithInfo> findAllWithPaging(String code, String name, Date birthday , Integer limit, Integer offset, String orderByClause);

    /**
     * Counts students matching optional filters (for pagination totals).
     *
     * @param code optional code filter
     * @param name optional name filter
     * @param birthday optional birthday filter
     * @return total matching rows
     */
    @Select
    Long countAll(String code, String name, Date birthday);
}
