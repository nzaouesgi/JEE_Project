package fr.esgi.secureupload.common.ports;

public interface RandomTokenGenerator {
    String generate(int bytesLength);
}
