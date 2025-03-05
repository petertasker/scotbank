package uk.co.asepstrath.bank.services.login;

import io.jooby.Server;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Arrays;

public class HashingPasswordService {

    private static final int iteration = 100_000;
    private static final int salt_size = 16;
    private static final int key_Length = 256;
    private static final String algorithm = "PBKDF2WithHmacSHA256";

    private static byte[] generateSalt() {
        byte[] salt = new byte[salt_size];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String Password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        byte[] hashPassword = hashPassword(Password,salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hashPassword);
    }

    private static byte[] hashPassword(String Password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(Password.toCharArray(), salt, iteration, key_Length);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        return keyFactory.generateSecret(spec).getEncoded();
    }

    public static boolean verifyPassword(String Password, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hashedPassword.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedPassword = Base64.getDecoder().decode(parts[1]);
        byte[] recivedPassword = hashPassword(Password,salt);
        return Arrays.equals(expectedPassword, recivedPassword);
    }
}
