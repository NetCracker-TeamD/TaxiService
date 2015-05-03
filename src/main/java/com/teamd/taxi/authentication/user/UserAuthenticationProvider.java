package com.teamd.taxi.authentication.user;

import com.teamd.taxi.authentication.driver.DriverUsernamePasswordAuthenticationToken;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

public class UserAuthenticationProvider extends DaoAuthenticationProvider {


    private static final Logger logger = Logger.getLogger(UserAuthenticationProvider.class);

    @Override
    public boolean supports(Class<?> authentication) {
        boolean retVal = (UserUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
        logger.info("Am i support the: " + authentication.getName() + " : " + retVal);
        return retVal;
    }
}
