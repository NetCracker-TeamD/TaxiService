package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            "SELECT u FROM User u WHERE u.email = ?1 AND u.userRole IN (" +
                    "com.teamd.taxi.entity.UserRole.ROLE_CUSTOMER," +
                    "com.teamd.taxi.entity.UserRole.ROLE_ADMINISTRATOR)"
    )
    User findByEmail(String email);

    List<User> findByConfirmationCode(String confirmationCode);

    @Query("select u from User u where u.id not in (select g.user.id from GroupList g where g.userGroup.groupId = :groupId) and u.userRole = com.teamd.taxi.entity.UserRole.ROLE_CUSTOMER")
    List<User> getUserNotFromGroup(@Param("groupId") Integer id);
}
