package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Олег on 14.05.2015.
 */
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

    @Query("SELECT g FROM UserGroup g JOIN g.groups gl WHERE gl.user.id = ?1")
    List<UserGroup> findByUserId(long userId);

}
