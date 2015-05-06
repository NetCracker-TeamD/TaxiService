package com.teamd.taxi.authentication.driver;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

public class DriverAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger logger = Logger.getLogger(DriverAuthenticationProvider.class);

    @Override
    public boolean supports(Class<?> authentication) {
        boolean retVal = (DriverUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
        logger.info("Am i support the: " + authentication.getName() + " : " + retVal);
        return retVal;
    }
}
