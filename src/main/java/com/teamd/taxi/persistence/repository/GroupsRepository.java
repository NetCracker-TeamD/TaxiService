package com.teamd.taxi.persistence.repository;


import com.teamd.taxi.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupsRepository extends JpaRepository<UserGroup, Integer> {


}
