package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.BlackListItem;
import com.teamd.taxi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Олег on 24.05.2015.
 */
public interface BlackListItemRepository extends JpaRepository<BlackListItem, Integer>, CrudRepository<BlackListItem, Integer> {

    List<BlackListItem> findByUserId(long userId);

    Integer countByUserIdAndPayedTrue(Long userId);

    Long countByUserId(long userId);
}
