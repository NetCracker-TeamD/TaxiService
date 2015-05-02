package com.teamd.taxi.service;

import com.teamd.taxi.entity.User;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserAuthenticationService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(CustomerUserAuthenticationService.class);

    public CustomerUserAuthenticationService() {
        System.out.println(this.getClass() + " created at: " + System.currentTimeMillis());
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (userRepository == null) {
            logger.info("UserRepository is null");
            throw new UsernameNotFoundException(email);
        }
        User user = userRepository.findByEmail(email);
        logger.info("User: " + user);
        return null;
    }
}
