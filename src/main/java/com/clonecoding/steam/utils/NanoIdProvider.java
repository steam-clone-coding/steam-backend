package com.clonecoding.steam.utils;

import java.security.SecureRandom;

public class NanoIdProvider {
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int ID_LENGTH = 21;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String randomNanoId() {
        char[] buffer = new char[ID_LENGTH];
        for (int idx = 0; idx < ID_LENGTH; ++idx) {
            buffer[idx] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(buffer);
    }
}