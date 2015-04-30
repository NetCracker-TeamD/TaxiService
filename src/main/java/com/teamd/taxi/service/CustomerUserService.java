package com.teamd.taxi.service;

import com.teamd.taxi.entity.User;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserService {

    @Autowired
    private UserRepository userRepository;

    public void registerNewCustomerUser(User newUser) {

    }
}
