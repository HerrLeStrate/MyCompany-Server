package Routers;

import Utils.HttpParser;
import Utils.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginLink {

    public static void execute(HttpParser parser){
        String login = parser.getParam("login");
        String password = parser.getParam("password");

        System.out.println(login + " " + password);

        JSONObject jsonObject = new JSONObject();

        password = inMD5(password);
        System.out.println(password);

        ResultSet rs = Router.getMySQLWorker().doLogin(login, password);

        try {
            if (rs == null || !rs.next()) {
                jsonObject.put("result", "No such user or incorrect password!");
                Router.setAnswer(jsonObject);
                return;
            }

            Session session = new Session(login);
            session.setSession(Session.generateSession());

            jsonObject.put("result", "OK");
            jsonObject.put("session", session.getSession());
        } catch (SQLException ex){
            ex.printStackTrace();
            //Bad login
            jsonObject.put("result", "No such user or incorrect password!");
        }

        Router.setAnswer(jsonObject);
    }

    public static String inMD5(String st){

        return DigestUtils.md5Hex(st);
    }
}
