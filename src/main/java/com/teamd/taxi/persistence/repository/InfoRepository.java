package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.entity.Info;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Олег on 03.05.2015.
 */
public interface InfoRepository extends JpaRepository<Info, String> {
}
