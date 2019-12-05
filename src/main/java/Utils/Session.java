package Utils;

import Routers.Router;

import java.util.Random;

public class Session {

    private String login = "none";
    private String session = "none";
    private static String alphabet;

    {
        StringBuilder sb = new StringBuilder();

        for(char c = 'A'; c != 'Z'; c++)
            sb.append(c);

        for(char c = 'a'; c != 'z'; c++)
            sb.append(c);

        for(char c = '0'; c != '9'; c++)
            sb.append(c);

        alphabet = sb.toString();
    }

    public Session(String login) {
        this.login = login;
    }

    public Session(String string, boolean isSession){
       if(isSession){
           this.session = string;
           this.login = getLoginBySession();
       } else {
           login = string;
       }
    }

    public String getLoginBySession(){
        return Router.getMySQLWorker().getLoginBySession(this.session);
    }

    public String getPrivilegesBySession(){
        //TODO mysql getPrivilegesBySession
        return "";
    }

    public void resetSession(){
        setSession("none");

    }

    public void setSession(String session){
        this.session = session;
        Router.getMySQLWorker().setSession(login, session);
    }

    public String getLogin() {
        return this.login;
    }

    public String getSession(){
        return this.session;
    }

    public static String generateSession(){
        StringBuilder session = new StringBuilder();
        Random rnd = new Random();

        for(int i = 0; i < 128; i++){
            int element = Math.abs(rnd.nextInt() % alphabet.length());
            session.append(alphabet.charAt(element));
        }

        return session.toString();

    }

}
