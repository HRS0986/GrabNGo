package com.auth.auth.utils;

import java.util.Random;

public class VerificationCodeGenerator {
    public static String generateCode() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }
}
