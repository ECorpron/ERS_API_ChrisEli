package com.revature.util;

import javax.persistence.criteria.CriteriaBuilder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
    private static PasswordHash hash = new PasswordHash();

    public static PasswordHash getInstance() {
        return hash;
    }

    public String hashing(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte bit: bytes) {
                sb.append(Integer.toString((bit & 0xff)+ 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
