package com.jane.tutorials.junit.io;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmailEndsWith(String email);
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    @Query("select user from UserEntity user where user.email like %:emailDomain")
    List<UserEntity> findUsersWithEmailEndingWith(@Param("emailDomain") String emailDomain);
}
