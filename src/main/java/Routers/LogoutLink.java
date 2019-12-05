package Routers;

import Utils.HttpParser;
import Utils.Session;
import org.json.JSONObject;

public class LogoutLink {

    public static void execute(HttpParser parser){
        Session session = new Session(parser.getParam("session"), true);

        JSONObject jsonObject = new JSONObject();
        if(session.getLogin().equalsIgnoreCase("none")){
            jsonObject.put("result", "Session not found!");
        } else {
            session.resetSession();
            jsonObject.put("result", "OK");

        }

        Router.setAnswer(jsonObject);
    }
}
