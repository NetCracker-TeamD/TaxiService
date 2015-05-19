package com.teamd.taxi.service;

import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserAddress;
import com.teamd.taxi.persistence.repository.UserAddressRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ListResourceBundle;

/**
 * Created by Олег on 17.05.2015.
 */
@Service
public class UserAddressService {

    @Autowired
    private UserAddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserAddress> findAddressesByUserId(long userId) {
        return addressRepository.findByUserId(userId);
    }

    public UserAddress save(UserAddress userAddress) {
        return addressRepository.saveAndFlush(userAddress);
    }

    @Transactional
    public void delete(UserAddress userAddress) {
        User user = userRepository.findOne(userAddress.getUser().getId());
        user.getAddresses().remove(userAddress);
        addressRepository.delete(userAddress);
    }
}
