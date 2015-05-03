package com.teamd.taxi.authentication.driver;

import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.persistence.repository.DriverRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DriverDetailsService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(DriverDetailsService.class);

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Let me find the driver by: " + email);
        Driver driver = driverRepository.findByEmail(email);
        if (driver == null) {
            throw new UsernameNotFoundException(email);
        }
        return new AuthenticatedUser(driver);
    }
}
