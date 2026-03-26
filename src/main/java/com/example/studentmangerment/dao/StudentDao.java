package com.example.studentmangerment.dao;

import com.example.studentmangerment.entity.Student;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Optional;

@Dao
@ConfigAutowireable
public interface StudentDao {
    @Insert
    Result<Student> insert(Student student);

//    @Select
//    Optional<Student> findById(Integer id);


    @Select
    Optional<Student> findByCode(String code);
}
