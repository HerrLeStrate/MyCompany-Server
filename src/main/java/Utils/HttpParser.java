package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HttpParser {

    private HashMap<String, String> params = null;
    private HashMap<String, String> headers = null;
    private BufferedReader is = null;

    private String method = "";
    private String page = "";

    public HttpParser(InputStream is){
        this.is = new BufferedReader(new InputStreamReader(is));

        params = new HashMap<>();
        headers = new HashMap<>();
    }

    /**
     *
     * @return http code request for parsing
     */
    public int parse() {
        try {
            String initial = is.readLine();

            //if first string is a whitespace...
            if(initial == null || initial.length() == 0)
                return 400;

            String[] cmd = initial.split("\\s");

            //If cmd length is not 3(first GET/POST, second host, and third http protocol)
            if(cmd.length != 3)
                return 400;

            //TODO save version protocol in vars


            method = cmd[0];
            cmd[1] = cmd[1].substring(1);
            if(!cmd[1].contains("?")){
                page = cmd[1];
            } else {
                page = cmd[1].substring(0, cmd[1].indexOf("?"));
                String[] prms = cmd[1].substring(cmd[1].indexOf("?")+1).split("&");
                for(String param : prms){
                    String[] paramArgs = param.split("=");
                    if(paramArgs.length != 2){
                        return 400;
                    }
                    this.params.put(paramArgs[0], paramArgs[1]);
                }

            }

        }catch (IOException ex){
            //IOException
            return 400;
        }

        return 200;
    }

    //@Nullable
    public String getParam(String key){
        if(this.params.containsKey(key))
            return this.params.get(key);
        return null;
    }

    public String getMethod(){
        return this.method;
    }

    //@Nullable
    public String getPage(){
        return this.page;
    }
}
