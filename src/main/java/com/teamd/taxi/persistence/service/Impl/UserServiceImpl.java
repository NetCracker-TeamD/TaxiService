package com.teamd.taxi.persistence.service.Impl;

import com.teamd.taxi.entity.User;
import com.teamd.taxi.persistence.repository.UserRepository;
import com.teamd.taxi.persistence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    public User find(long id) {
        return userRepository.findOne(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
