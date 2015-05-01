package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findByConfirmationCode(String confirmationCode);
}
