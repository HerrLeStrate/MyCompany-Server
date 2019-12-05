package Routers;

import Utils.HttpParser;
import Utils.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewsLink {

    public static void execute(HttpParser parser){
        Session session = new Session(parser.getParam("session"), true);

        JSONObject jsonObject = new JSONObject();
        if(session.getLogin().equalsIgnoreCase("none")){
            //Not founded
            jsonObject.put("result", "session not found");

            Router.setAnswer(jsonObject);

            return;
        }

        ResultSet rs = Router.getMySQLWorker().getNews();

        try {
            if (rs == null || !rs.next()) {
                jsonObject.put("result", "No news!");

                Router.setAnswer(jsonObject);

                return;
            }

            JSONArray arr = new JSONArray();
            List<String[]> list = new ArrayList<>();


            do{
                list.add(new String[]{
                        rs.getString("id"),
                        rs.getString("author"),
                        rs.getString("news"),
                        rs.getString("time")
                });
            }while (rs.next());

            for(String[] arg : list){
                JSONObject temp = new JSONObject();

                temp.put("id", arg[0]);
                temp.put("author", arg[1]);
                temp.put("news", arg[2]);
                temp.put("time", arg[3]);

                arr.put(temp);
            }

            jsonObject.put("result", "OK");
            jsonObject.put("data", arr);
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        Router.setAnswer(jsonObject);
    }
}
