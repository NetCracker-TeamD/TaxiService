package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            "SELECT u FROM User u WHERE u.email = ?1 AND u.userRole IN (" +
                    "com.teamd.taxi.entity.UserRole.ROLE_CUSTOMER," +
                    "com.teamd.taxi.entity.UserRole.ROLE_ADMINISTRATOR)"
    )
    User findByEmail(String email);

    List<User> findByConfirmationCode(String confirmationCode);
}
