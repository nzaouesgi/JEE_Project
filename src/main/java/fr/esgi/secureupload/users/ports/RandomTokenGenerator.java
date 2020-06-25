package fr.esgi.secureupload.users.ports;

public interface RandomTokenGenerator {
    String generate(int bytesLength);
}
