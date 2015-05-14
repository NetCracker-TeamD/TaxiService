package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Олег on 14.05.2015.
 */
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
}
