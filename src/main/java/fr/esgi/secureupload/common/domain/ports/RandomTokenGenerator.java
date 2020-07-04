package fr.esgi.secureupload.common.domain.ports;

public interface RandomTokenGenerator {
    String generate(int bytesLength);
}
