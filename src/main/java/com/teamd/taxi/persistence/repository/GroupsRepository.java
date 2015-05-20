package com.teamd.taxi.persistence.repository;


import com.teamd.taxi.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
<<<<<<< HEAD

public interface GroupsRepository extends JpaRepository<UserGroup, Integer> {


=======

public interface GroupsRepository extends JpaRepository<UserGroup, String> {

    @Query("SELECT g FROM UserGroup g JOIN g.groups gl WHERE gl.user.id = ?1")
    List<UserGroup> findByUserId(long userId);
>>>>>>> 4d9d8d9a486f99779535e18406985c572f8be9aa
}
