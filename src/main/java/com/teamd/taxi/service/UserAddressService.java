package com.teamd.taxi.service;

import com.teamd.taxi.entity.UserAddress;
import com.teamd.taxi.persistence.repository.UserAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListResourceBundle;

/**
 * Created by Олег on 17.05.2015.
 */
@Service
public class UserAddressService {

    @Autowired
    private UserAddressRepository addressRepository;

    public List<UserAddress> findAddressesByUserId(long userId) {
        return addressRepository.findByUserId(userId);
    }
}
