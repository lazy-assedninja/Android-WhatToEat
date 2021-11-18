package me.lazy_assedninja.library.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class EncryptUtils {

    @Inject
    public EncryptUtils() {
    }

    @Nullable
    public String sha256(String data) {
        return encrypt("SHA-256", data);
    }

    @Nullable
    private static String encrypt(String algorithm, String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] encryption = md.digest(data.getBytes());

            // Byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encryption) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}