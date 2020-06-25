package fr.esgi.secureupload.common.utils;

import java.security.SecureRandom;


public class Utils {

    public static String randomBytesToHex(int bytesLength){

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[bytesLength];
        random.nextBytes(bytes);

        StringBuilder out = new StringBuilder();
        for (byte b : bytes) {
            out.append(String.format("%02X", b));
        }

        return out.toString();
    }

}
