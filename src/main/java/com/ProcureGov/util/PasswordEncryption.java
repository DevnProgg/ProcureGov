package com.ProcureGov.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordEncryption {
    private static final int SALT_LENGTH = 16; // bytes
    private static final int HASH_LENGTH = 256; // bits
    private static final int ITERATIONS = 65536;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Hashes a password securely using PBKDF2 with HmacSHA256 and a random, cryptographically secure salt.
     * The result is a Base64 encoded string in the format "salt:hash".
     *
     * @param password The plain text password provided by the user.
     * @return The securely hashed password string, including the salt, in Base64 format.
     * @throws RuntimeException if an underlying cryptographic error occurs during the hashing process.
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            secureRandom.nextBytes(salt);

            byte[] hash = pbkdf2(password.toCharArray(), salt);

            return Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    /**
     * Verifies a plain text password against a stored hash string retrieved from the database.
     * It extracts the salt and hash, computes a new hash, and performs a constant-time comparison.
     *
     * @param password The plain text password to verify.
     * @param storedHash The stored password string in the format "salt:hash" (Base64 encoded).
     * @return {@code true} if the password matches the stored hash, {@code false} otherwise (including on error).
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            byte[] testHash = pbkdf2(password.toCharArray(), salt);

            /**
             * Performs a constant-time comparison of the computed hash and the stored hash
             * to mitigate timing attacks.
             */
            if (hash.length != testHash.length) return false;
            int diff = 0;
            for (int i = 0; i < hash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a password hash using the PBKDF2 (Password-Based Key Derivation Function 2) algorithm.
     *
     * @param password The password characters array.
     * @param salt The salt bytes.
     * @return The derived key (hash) bytes.
     * @throws NoSuchAlgorithmException If the PBKDF2WithHmacSHA256 algorithm is not supported.
     * @throws InvalidKeySpecException If the key specification is invalid.
     */
    private static byte[] pbkdf2(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, HASH_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }
//
//    public static void main(String[] args){
//        System.out.println("Password - Khalapa@123 | Hash - " + hashPassword("Khalapa@123"));
//        System.out.println("Password - Cookie@123 | Hash - " + hashPassword("Cookie@123"));
//        System.out.println("Password - Dolphie@333 | Hash - " + hashPassword("Dolphie@333"));
//        System.out.println("Password - Crazy@$44 | Hash - " + hashPassword("Crazy@$44"));
//        System.out.println("Password - Poopie.@34 | Hash - " + hashPassword("Poopie.@34"));

//        System.out.println(verifyPassword("Khalapa@123", "NgLYGeukSgG7DkBtvEvLHA==:H08Q9pIdYCQu8/WmueoNFpzpf2OCrzaOetyrKgqJ7ag="));
//    }
}
