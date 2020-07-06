package fr.esgi.secureupload.utils;

import fr.esgi.secureupload.common.infrastructure.adapters.helpers.SecureRandomTokenGenerator;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.users.infrastructure.adapters.helpers.UserPasswordEncoderImpl;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.common.domain.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.domain.ports.UserPasswordEncoder;
import fr.esgi.secureupload.utils.URLReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
public class TestUtils {

    @Value("${spring.mail.host}")
    private String smtpHost;

    private UserPasswordEncoder encoder;
    private RandomTokenGenerator generator;

    public static final String DEFAULT_PASSWORD = "MyPassword12345";

    public TestUtils(@Autowired PasswordEncoder springEncoderBean){
        this.encoder = new UserPasswordEncoderImpl(springEncoderBean);
        this.generator = new SecureRandomTokenGenerator();
    }

    public JSONObject getSentMail(String to) throws JSONException, IOException {

        // Read sent message for Mailhog API.
        JSONArray messages = new JSONArray(URLReader.readStringFromUrl("http://" + this.smtpHost + ":8025/api/v1/messages"));

        JSONObject sentMessage = null;

        for (int i = 0; i < messages.length(); i ++){
            if (messages.getJSONObject(i)
                    .getJSONObject("Content")
                    .getJSONObject("Headers")
                    .getJSONArray("To")
                    .getString(0).equals(to)){
                sentMessage = messages.getJSONObject(i).getJSONObject("Content");
            }
        }
        return sentMessage;
    }

    public User getRandomUser (boolean admin){
        return User.builder()
                .admin(admin)
                .confirmed(true)
                .password(this.encoder.encode(DEFAULT_PASSWORD))
                .confirmationToken(this.generator.generate(32))
                .email(this.getRandomMail())
                .build();
    }

    public File getRandomFile (User user){
        return new File(getRandomFileName(), "application/octet-stream", (new Random()).nextInt(1000) + 1, FileStatus.READY, user == null ? getRandomUser(false) : user);
    }

    public String getRandomFileName (){
        return String.format("%s.file", this.generator.generate(10));
    }

    public String getRandomMail (){
        return String.format("%suser@domain.fr", this.generator.generate(6));
    }
}
