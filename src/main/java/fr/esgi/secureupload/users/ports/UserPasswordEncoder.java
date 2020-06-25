package fr.esgi.secureupload.users.ports;

public interface UserPasswordEncoder {
    String encode(CharSequence password);
    boolean verify(CharSequence password, String hash);
}
