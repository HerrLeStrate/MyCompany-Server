package Routers;

import MySQL.MySQLWorker;
import Utils.HttpParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetLink {

    public static void execute(HttpParser parser){

        String clientId = parser.getParam("clientId");

        MySQLWorker msql = Router.getMySQLWorker();

        String date = msql.getLastQuery(clientId);
        msql.insertLastQuery(clientId);

        List<String[]> list = new ArrayList<>();
        list = msql.getData(clientId, date);
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for(String[] ls : list){
            String time = ls[0];
            Long ePower = Long.parseLong(ls[1]);
            JSONObject temp = new JSONObject();
            temp.put("time", time);
            temp.put("ePower", ePower);

            jsonArray.put(temp);
        }

        json.put("data", jsonArray);

        Router.setAnswer(json);
    }
}
