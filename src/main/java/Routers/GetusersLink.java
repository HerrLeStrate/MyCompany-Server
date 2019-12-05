package Routers;

import Utils.HttpParser;
import Utils.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetusersLink {

    public static void execute(HttpParser parser){
        Session session = new Session(parser.getParam("session"), true);

        JSONObject jsonObject = new JSONObject();
        if(session.getLogin().equalsIgnoreCase("none")){
            //Not founded
            jsonObject.put("result", "session not found");

            Router.setAnswer(jsonObject);

            return;
        }

        //TODO return userlist if user have rights for this

        ResultSet rs = Router.getMySQLWorker().getUsers();

        try {
            if (rs == null || !rs.next()) {
                jsonObject.put("Result", "users not found!");

                Router.setAnswer(jsonObject);

                return;
            }

            List<String[]> list = new ArrayList<>();

            do{
                list.add(new String[]{
                        rs.getString("login"),
                        rs.getString("email"),
                        rs.getString("group")
                });
            } while (rs.next());

            jsonObject.put("result", "OK");

            JSONArray arr = new JSONArray();

            for(String[] arg : list){
                JSONObject object = new JSONObject();
                object.put("login", arg[0]);
                object.put("email", arg[1]);
                object.put("group", arg[2]);
                arr.put(object);
            }
            jsonObject.put("data", arr);
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        Router.setAnswer(jsonObject);
    }
}
