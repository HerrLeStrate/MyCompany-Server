package Routers;

import Utils.HttpParser;
import org.json.JSONObject;

public class LoginLink {

    public static void execute(HttpParser parser){
        String login = parser.getParam("login");
        String password = parser.getParam("password");

        JSONObject jsonObject = new JSONObject();
        if(login.equalsIgnoreCase("root"))
            jsonObject.put("Validation", "true");
        else {
            jsonObject.put("Validation", "false");
            jsonObject.put("Reason", "Non-root user");
        }

        Router.setAnswer(jsonObject);

        //TODO mysql login
    }
}
