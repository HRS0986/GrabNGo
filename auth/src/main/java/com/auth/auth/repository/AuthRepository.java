package com.auth.auth.repository;
import com.auth.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.emailAddress = ?1")
    User findByEmail(String email);

}


