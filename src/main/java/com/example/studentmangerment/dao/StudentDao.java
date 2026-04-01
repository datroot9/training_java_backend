package com.example.studentmangerment.dao;

import com.example.studentmangerment.entity.Student;
import com.example.studentmangerment.entity.StudentWithInfo;
import org.seasar.doma.*;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface StudentDao {
    @Insert
    Result<Student> insert(Student student);

    @Select
    Optional<Student> findById(Integer id);

    @Select
    Optional<StudentWithInfo> findStudentWithInfoById(Integer id);

    @Select
    Optional<Student> findByCode(String code);

    @Select
    Optional<StudentWithInfo> findWithInfoByCode(String code);

    @Update
    Result<Student> update(Student student);

    @Delete
    Result<Student> delete(Student student);

    @Select
    List<StudentWithInfo> findAllWithPaging(String code, String name, Date birthday , Integer limit, Integer offset, String orderByClause);

    @Select
    Long countAll(String code, String name, Date birthday);
}
