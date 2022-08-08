package chads.util;

import chads.model.User;
import org.json.JSONObject;


import java.util.Base64;

public class JwtUtils {
    public static User getUserFromJwt(String googleJwt) {
        String base64payload = googleJwt.split("\\.")[1];
        JSONObject payload = new JSONObject(decode(base64payload));
        String userId = payload.getString("sub");
        String first_name = payload.getString("given_name");
        String last_name = payload.getString("family_name");
        String email = payload.getString("email");
        String username = email.split("@")[0];
        return new User(username, userId, first_name, last_name, email);
    }

    private static String decode(String googleJwt) {
        return new String(Base64.getUrlDecoder().decode(googleJwt));
    }
}
