package com.teamd.taxi.service;

import com.teamd.taxi.controllers.IndexAndRegistrationController;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserRole;
import com.teamd.taxi.exception.UserAlreadyConfirmedException;
import com.teamd.taxi.persistence.repository.UserRepository;
import com.teamd.taxi.service.email.MailService;
import com.teamd.taxi.service.email.Notification;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Random;

@Service
public class CustomerUserService {

    private static final Logger logger = Logger.getLogger(CustomerUserService.class);

    private static int CONFIRMATION_CODE_LENGTH = 60;

    @Autowired
    private RandomStringGenerator stringGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService service;

    @Autowired
    private PasswordEncoder encoder;


    public User registerNewCustomerUser(User newUser) throws MessagingException {
        //create confirmation code for user and attach it to the entity
        newUser.setConfirmed(false);
        String confirmationCode = null;
        //we have to generate unique code
        //probably, only one generation will be performed,
        //because of huge amount (36^60 ~ 2 ^ 310) of possible codes
        do {
            confirmationCode = stringGenerator.generateString(CONFIRMATION_CODE_LENGTH);
        } while (userRepository.findByConfirmationCode(confirmationCode).size() > 0);
        newUser.setConfirmationCode(confirmationCode);
        newUser.setUserRole(UserRole.ROLE_CUSTOMER);
        newUser.setUserPassword(encoder.encode(newUser.getUserPassword()));
        //save it
        newUser = userRepository.save(newUser);
        //notification email
        service.sendNotification(newUser.getEmail(), Notification.REGISTRATION, generateConfirmationURL(confirmationCode));

        logger.info("User[" + newUser.getId() + "]" +
                " registered with [" + confirmationCode + "] confirmation code");
        return newUser;
    }

    private String generateConfirmationURL(String confirmationCode) {
        String url = MvcUriComponentsBuilder
                .fromMethodName(IndexAndRegistrationController.class, "confirmUser", confirmationCode, null)
                .toUriString();
        System.out.println("url = " + url);
        return url;
    }

    public boolean confirmUser(String confirmationCode) throws UserAlreadyConfirmedException {
        List<User> userList = userRepository.findByConfirmationCode(confirmationCode);
        int size = userList.size();
        if (size == 1) {
            User user = userList.get(0);
            if (user.isConfirmed()) {
                throw new UserAlreadyConfirmedException();
            }
            user.setConfirmed(true);
            userRepository.save(user);
            logger.info("User[" + user.getId() + "] confirmed");
            return true;
        } else if (size > 1) {
            logger.error("More than one user have confirmation code: " + confirmationCode);
        }
        return false;
    }

    public User findById(Long id) {
        return userRepository.findOne(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
