package fr.esgi.secureupload.utils;

import java.security.SecureRandom;

import static com.kosprov.jargon2.api.Jargon2.*;

public class Crypto {

    public static String randomString (int bytesLength){

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[bytesLength];
        random.nextBytes(bytes);

        StringBuilder out = new StringBuilder();
        for (byte b : bytes) {
            out.append(String.format("%02X", b));
        }

        return out.toString();
    }

    public static String passwordHash (String clearPassword){
        Hasher hasher = jargon2Hasher()
                .type(Type.ARGON2id)
                .memoryCost(65536)
                .timeCost(3)
                .parallelism(4)
                .saltLength(16)
                .hashLength(16);

        return  hasher.password(clearPassword.getBytes()).encodedHash();
    }

    public static boolean verifyPassword (String hash, String password) {
        Verifier verifier = jargon2Verifier();
        return verifier.hash(hash).password(password.getBytes()).verifyEncoded();
    }
}
