package Routers;

public class LinkNotFoundException extends Exception {

    public LinkNotFoundException(){
        super();
    }

    public LinkNotFoundException(String msg){
        super(msg);
    }
}
