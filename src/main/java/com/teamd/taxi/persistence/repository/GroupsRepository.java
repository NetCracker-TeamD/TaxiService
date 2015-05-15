package com.teamd.taxi.persistence.repository;


import com.teamd.taxi.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupsRepository extends JpaRepository<UserGroup, String> {
}
