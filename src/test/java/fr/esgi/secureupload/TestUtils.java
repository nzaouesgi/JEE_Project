package fr.esgi.secureupload;

import fr.esgi.secureupload.utils.URLReader;
import fr.esgi.secureupload.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TestUtils {

    @Value("${spring.mail.host}")
    private String mailhogHost;

    public JSONObject getSentMail(String to) throws JSONException, IOException {

        // Read sent message for Mailhog API.
        JSONArray messages = new JSONArray(URLReader.readStringFromUrl(String.format("http://%s:8025/api/v1/messages", this.mailhogHost)));

        JSONObject sentMessage = null;

        for (int i = 0; i < messages.length(); i ++){
            if (messages.getJSONObject(i)
                    .getJSONObject("Content")
                    .getJSONObject("Headers")
                    .getJSONArray("To")
                    .getString(0).compareTo(to) == 0){
                sentMessage = messages.getJSONObject(i).getJSONObject("Content");
            }
        }
        return sentMessage;
    }

    public String getRandomMail (){
        return String.format("%suser@domain.fr", Utils.randomString(3));
    }
}
