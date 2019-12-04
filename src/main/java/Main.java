import Routers.LinkNotFoundException;
import Routers.Router;
import Utils.HttpParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static ServerSocket serverSocket = null;
    private static int port = 8080;
    private static int maxConnections = 50;

    public static void main(String[] args){
        loadConfiguration("config.json");

        try {

            //В блоке создаем сервер и проверяем, существует ли порт для его создания.
            try {
                serverSocket = new ServerSocket(port, maxConnections);
            } catch(SecurityException exception){
                System.out.println("Its looks like that this port already used... Do you think so?");
                exception.printStackTrace();
                System.exit(0);
            }

            //Для обработки всех поступающих запросов
            while(true) {
                //Как только получили соединение...
                Socket clientSocket = serverSocket.accept();


                //Создание потоков I/O
                try {
                    HttpParser parser = new HttpParser(clientSocket.getInputStream());

                    parser.parse();

                    try {
                        String page = parser.getPage();
                        if(page == null || page.length() == 0) throw new LinkNotFoundException();

                        Router.link(page, parser);

                        try (OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream())) {
                            out.write(Router.getAnswer());
                        }

                    }catch (LinkNotFoundException ex){
                        //Ignore it
                    }

                } finally {
                    clientSocket.close();
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Some errors. Stopped!");
            System.exit(0);
        }


    }

    private static void loadConfiguration(String filePath){
        System.out.println("Load configuration...");
        File config = new File(filePath);
        if(!config.exists()){
            URL url = Thread.currentThread().getContextClassLoader().getResource("config.json");
            try {
                Path path = Paths.get(url.toURI());
                List<String> lines = Files.readAllLines(path);
                boolean result = config.createNewFile();
                if(!result){
                    System.out.println("Some errors while creating " + filePath + "...");
                    System.exit(0);
                }
                FileWriter fw = new FileWriter(config);
                for(String line : lines) {
                    fw.write(line + "\n");
                }

                fw.flush();
                fw.close();
            } catch( URISyntaxException | IOException ex){
                ex.printStackTrace();
            }
            System.exit(0);
        }
        //Read host and port
        StringBuilder sb = new StringBuilder();
        try {
            Scanner sc = new Scanner(config);
            while (sc.hasNextLine())
                sb.append(sc.nextLine());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject jsonConfig = new JSONObject(sb.toString().trim());
            JSONObject serverOptions = jsonConfig.getJSONObject("server");
            port = serverOptions.getInt("port");
            maxConnections = serverOptions.getInt("maxConnections");

            System.out.println("Load MySQL data");

            JSONObject mysqlOptions = jsonConfig.getJSONObject("mysql");
            Router.loadMySQL(new String[]{
                    mysqlOptions.getString("hostname"),
                    mysqlOptions.getString("port"),
                    mysqlOptions.getString("database"),
                    mysqlOptions.getString("username"),
                    mysqlOptions.getString("password")
            });
        } catch(JSONException ex){
            ex.printStackTrace();
            System.out.println("Some errors with config. Please, remove and start application.");
            System.exit(1);
        }

        System.out.println("Loaded!");
    }

    public Main getInstance(){
        return this;
    }
}
