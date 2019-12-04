package Routers;

import MySQL.MySQLWorker;
import Utils.HttpParser;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Router {

    private static MySQLWorker MySQLWorker;
    private static JSONObject answer = null;

    public static void loadMySQL(String[] args){
        MySQLWorker = new MySQLWorker(args[0],
                args[1],
                args[2],
                args[3],
                args[4]);
    }

    public static void link(String page, HttpParser parser) throws LinkNotFoundException{
        String classname = "Routers." + page.substring(0,1).toUpperCase() +
                page.substring(1).toLowerCase() + "Link";
        try {
            Class<?> linkClass = Class.forName(classname);
            Method execute = linkClass.getDeclaredMethod("execute", HttpParser.class);

            execute.invoke(linkClass, parser);
        } catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new LinkNotFoundException(e.getMessage());
        }
    }

    public static MySQLWorker getMySQLWorker() {
        return MySQLWorker;
    }

    public static String getAnswer() {
        return (answer == null) ? "{\"result\": \"none\"}" : answer.toString() ;
    }

    public static void setAnswer(JSONObject answer) {
        Router.answer = answer;
    }
}
