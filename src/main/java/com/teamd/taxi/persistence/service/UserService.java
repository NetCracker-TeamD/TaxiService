package com.teamd.taxi.persistence.service;

import com.teamd.taxi.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User find(long id);
    List<User> findAll();
}
