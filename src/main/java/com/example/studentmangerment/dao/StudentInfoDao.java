package com.example.studentmangerment.dao;

import com.example.studentmangerment.entity.StudentInfo;
import com.example.studentmangerment.entity.User;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Optional;

@Dao
@ConfigAutowireable
public interface StudentInfoDao {
    @Insert
    Result<StudentInfo> insert(StudentInfo studentInfo);

    @Select
    Optional<StudentInfo> findByStudentId(Integer studentId);
}
