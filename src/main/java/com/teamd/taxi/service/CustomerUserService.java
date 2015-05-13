package com.teamd.taxi.service;

import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserRole;
import com.teamd.taxi.exception.UserAlreadyConfirmedException;
import com.teamd.taxi.persistence.repository.UserRepository;
import com.teamd.taxi.service.email.EmailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Random;

@Service
public class CustomerUserService {

    private static final Logger logger = Logger.getLogger(CustomerUserService.class);

    private static int CONFIRMATION_CODE_LENGTH = 60;

    private static String CONFIRMATION_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder encoder;


    public User registerNewCustomerUser(User newUser) {
        //create confirmation code for user and attach it to the entity
        newUser.setConfirmed(false);
        String confirmationCode = null;
        //we have to generate unique code
        //probably, only one generation will be performed,
        //because of huge amount (36^60 ~ 2 ^ 310) of possible codes
        do {
            confirmationCode = generateConfirmationCode();
        } while (userRepository.findByConfirmationCode(confirmationCode).size() > 0);
        newUser.setConfirmationCode(confirmationCode);
        newUser.setUserRole(UserRole.ROLE_CUSTOMER);
        newUser.setUserPassword(encoder.encode(newUser.getUserPassword()));
        //TODO:send notification email
        //save it
        newUser = userRepository.save(newUser);
        logger.info("User[" + newUser.getId() + "]" +
                " registered with [" + confirmationCode + "] confirmation code");
        return newUser;
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

    private String generateConfirmationCode() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(CONFIRMATION_CODE_LENGTH);
        int alphabetLength = CONFIRMATION_CODE_ALPHABET.length();
        for (int i = 0; i < CONFIRMATION_CODE_LENGTH; i++) {
            int charIndex = random.nextInt(alphabetLength);
            builder.append(CONFIRMATION_CODE_ALPHABET.charAt(charIndex));
        }
        return builder.toString();
    }
}
