package Routers;

import MySQL.MySQLWorker;
import Utils.HttpParser;
import org.json.JSONObject;

public class AddLink {

    public static void execute(HttpParser parser){
        //Id of client
        String clientId = parser.getParam("clientId");
        //Total power(for all time)
        String ePower = parser.getParam("ePower");

        MySQLWorker msql = Router.getMySQLWorker();

        msql.insertData(clientId, Long.parseLong(ePower));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", msql.getNotification(clientId));

        Router.setAnswer(jsonObject);
    }
}
