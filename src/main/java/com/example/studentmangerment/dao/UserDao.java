package com.example.studentmangerment.dao;

import com.example.studentmangerment.entity.User;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Optional;

/**
 * Doma DAO for {@link User} persistence.
 */
@Dao
@ConfigAutowireable
public interface UserDao {
    /**
     * Inserts a new user.
     *
     * @param user entity to insert
     * @return Doma result with generated id
     */
    @Insert
    Result<User> insert(User user);

    /**
     * Finds a user by login name.
     *
     * @param username unique username
     * @return user if found
     */
    @Select
    Optional<User> findByUsername(String username);
}
