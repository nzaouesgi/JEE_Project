package fr.esgi.secureupload.common.adapters.helpers;

import fr.esgi.secureupload.users.ports.RandomTokenGenerator;

import java.security.SecureRandom;

public class SecureRandomTokenGenerator implements RandomTokenGenerator {

    @Override
    public String generate(int bytesLength) {
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
