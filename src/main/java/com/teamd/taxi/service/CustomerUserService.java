package com.teamd.taxi.service;

import com.teamd.taxi.entity.User;
import com.teamd.taxi.persistence.repository.UserRepository;
import com.teamd.taxi.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CustomerUserService {

    private static int CONFIRMATION_CODE_LENGTH = 60;

    private static String CONFIRMATION_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public User registerNewCustomerUser(User newUser) {
        //create confirmation code for user and attach it to the entity
        newUser.setIsConfirmed(false);
        String confirmationCode = null;
        //we have to generate unique code
        //probably, only one generation will be performed
        do {
            confirmationCode = generateConfirmationCode();
        } while (userRepository.findByConfirmationCode(confirmationCode) != null);
        newUser.setConfirmationCode(confirmationCode);
        //send notification email
        //save it
        return userRepository.save(newUser);
    }

    public boolean confirmUser(String confirmationCode) {
        User user = userRepository.findByConfirmationCode(confirmationCode);
        if (user != null) {
            user.setIsConfirmed(true);
            userRepository.save(user);
            return true;
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
