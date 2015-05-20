package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Олег on 03.05.2015.
 */
public interface InfoRepository extends JpaRepository<Info, String> {

    @Query("SELECT inf.value FROM Info inf where inf.name = (?1)")
    String findFreeTimeIdle(String name);
}
