package com.teamd.taxi.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created on 13-May-15.
 *
 * @author Nazar Dub
 */
@Service
public class RandomStringGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be positive integer or zero, but length = " + length);
        }
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        int alphabetLength = ALPHABET.length();
        for (int i = 0; i < length; i++) {
            int charIndex = random.nextInt(alphabetLength);
            builder.append(ALPHABET.charAt(charIndex));
        }
        return builder.toString();
    }

    public static void main(String[] args) {

    }

}
