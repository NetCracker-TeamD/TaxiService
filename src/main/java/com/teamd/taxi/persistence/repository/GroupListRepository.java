package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.GroupListPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by vita on 5/15/15.
 */
public interface GroupListRepository extends JpaRepository<GroupList, GroupListPK> {
    public List<GroupList> findByUserGroupGroupId(Integer id);
}
