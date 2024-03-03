package com.poppin.poppinserver.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordUtil {
    private static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*";
    private static final String ALL_ALLOWED_CHARACTERS = LOWER_CASE_LETTERS + UPPER_CASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int PASSWORD_LENGTH = 12; // 비밀번호 길이를 12로 증가

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_ALLOWED_CHARACTERS.charAt(RANDOM.nextInt(ALL_ALLOWED_CHARACTERS.length())));
        }
        return password.toString();
    }
}
