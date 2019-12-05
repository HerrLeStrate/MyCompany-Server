package MySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLWorker {

   private MySQL mysql;
   private final String host;
   private final String port;
   private final String user;
   private final String password;
   private final String database;


   public MySQLWorker(String Host, String Port, String Database2, String User, String Password) {
      this.host = Host;
      this.port = Port;
      this.database = Database2;
      this.user = User;
      this.password = Password;
      this.Connect();
      this.TryToCreateTableClass();
      this.TryToCreateTableData();
      this.TryToCreateTableNotification();
      this.TryToCreateTableUpdated();
      this.TryToCreateTableNews();
   }

    private void Connect() {
        this.mysql = new MySQL(this.host, this.port, this.database, this.user, this.password);
        try {
            this.mysql.openConnection();
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

   private void TryToCreateTableClass() {
      try {
         mysql.updateSQL("CREATE TABLE IF NOT EXISTS" +
                 " users" +
                 " (`id` INT NOT NULL AUTO_INCREMENT," +
                 " `login` TEXT(32) NOT NULL," +
                 " `password` TEXT NULL," +
                 " `email` TEXT(32) NOT NULL," +
                 " `session` TEXT(128) NULL," +
                 " `group` TEXT(32) DEFAULT 'guest'," +
                 " PRIMARY KEY(`id`)" +
                 ");");
      } catch (SQLException | ClassNotFoundException ex) {
          ex.printStackTrace();
      }
   }

   private void TryToCreateTableNews() {
       try {
           mysql.updateSQL("CREATE TABLE IF NOT EXISTS" +
                   " news" +
                   " (`id` INT NOT NULL AUTO_INCREMENT," +
                   " `author` INT NOT NULL," +
                   " `news` LONGTEXT NOT NULL," +
                   " `time` DATETIME NOT NULL," +
                   " PRIMARY KEY(`id`)" +
                   ");");
       } catch (SQLException | ClassNotFoundException ex) {
           ex.printStackTrace();
       }
   }

    private void TryToCreateTableData() {
        try {
            mysql.updateSQL("CREATE TABLE IF NOT EXISTS" +
                    " data" +
                    " (`id` INT NOT NULL AUTO_INCREMENT," +
                    " `clientId` TEXT(256) NOT NULL," +
                    " `ePower` BIGINT NOT NULL," +
                    " `time` DATETIME NOT NULL," +
                    " PRIMARY KEY(`id`)" +
                    ");");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void TryToCreateTableNotification() {
        try {
            mysql.updateSQL("CREATE TABLE IF NOT EXISTS" +
                    " notifications" +
                    " (`clientId` TEXT(256) NOT NULL," +
                    " `message` TEXT DEFAULT 'OK'" +
                    ");");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void TryToCreateTableUpdated() {
        try {
            mysql.updateSQL("CREATE TABLE IF NOT EXISTS" +
                    " updated" +
                    " (`clientId` TEXT(256) NOT NULL," +
                    " `updated` DATETIME NOT NULL DEFAULT NOW()" +
                    ");");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

   private void checkConnection() throws SQLException, ClassNotFoundException{
       if(!mysql.checkConnection()) {
           mysql.openConnection();
       }
   }

   public ResultSet getNews() {
       try {
           checkConnection();

           String query = "SELECT * FROM news ORDER BY id DESC;";

           PreparedStatement ps = mysql.connection.prepareStatement(query);

           return ps.executeQuery();

       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
       return null;
   }

   public String getLastQuery(String clientId){
       try{
           checkConnection();

           String query = "SELECT updated FROM updated WHERE clientId = ?;";

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, clientId);

           ResultSet rs = ps.executeQuery();
           if(rs == null || !rs.next()) return (new Date(0)).toString();

           return rs.getString("updated");
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
       return "none";
   }

   public void insertLastQuery(String clientId){
       try{
            checkConnection();

            if(!getLastQuery(clientId).equalsIgnoreCase("none"))
               updateLastQuery(clientId);
            String query = "INSERT INTO updated(clientId, updated) VALUES (?, NOW());";

            PreparedStatement ps = mysql.connection.prepareStatement(query);
            ps.setString(1, clientId);

            ps.execute();
       }catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
   }

   public void updateLastQuery(String clientId){
       try{
           checkConnection();

            String query = "Update `updated` SET updated = NOW() WHERE clientId = ?;";

            PreparedStatement ps = mysql.connection.prepareStatement(query);
            ps.setString(1, clientId);

            ps.execute();
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
   }

   public List<String[]> getData(String clientId, String from){
       try{
           checkConnection();

           String query = "SELECT ePower, time from data WHERE clientId = ? AND time >= ?;";

           PreparedStatement ps = mysql.connection.prepareStatement(query);

           ps.setString(1, clientId);
           ps.setString(2, from);

           ResultSet rs = ps.executeQuery();

           List<String[]> list = new ArrayList<>();
           while(rs.next()){
               list.add(new String[]{rs.getString("time"), rs.getString("ePower")});
           }

           return list;
       }catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
       return null;
   }

   public void insertNotification(String clientId, String message){
       try{
           checkConnection();
           String query = "INSERT INTO `notifications`(clientId, message) VALUES (?, ?);";
           if(!getNotification(clientId).equalsIgnoreCase("not found!"))
               updateNotification(clientId, message);

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, clientId);
           ps.setString(2, message);

           ps.execute();
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
   }

    public void updateNotification(String clientId, String message){
        try{
            checkConnection();
            String query = "UPDATE `notifications` SET message = ? WHERE clientId = ?;";

            PreparedStatement ps = mysql.connection.prepareStatement(query);
            ps.setString(1, message);
            ps.setString(2, clientId);

            ps.execute();
        } catch (SQLException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

   public String getNotification(String clientId){
       try{
           checkConnection();

           String query = "SELECT message FROM `notifications` WHERE clientId = ?;";

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, clientId);

           ResultSet rs = ps.executeQuery();
           rs.first();
           return rs.getString("message");

       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
       return "Not founded!";
   }

   public void insertData(String clientId, long value){
       try{
           checkConnection();

           String query = "INSERT INTO `data`(clientId, ePower, time) VALUES (?, ?, NOW());";

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, clientId);
           ps.setLong(2, value);

           ps.execute();
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
   }

   public ResultSet doLogin(String login, String password){
       try{
           checkConnection();

           String query = "SELECT * FROM `users` WHERE `login` = ? AND `password` = ? LIMIT 1";

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, login);
           ps.setString(2, password);

           return ps.executeQuery();

       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
           return null;
       }
   }

   public String getLoginBySession(String session){
       try {
           checkConnection();

           String query = "SELECT login from users where `session` = ?";

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, session);

           ResultSet rs = ps.executeQuery();

           if(rs == null || !rs.next())
               return "none";

           return rs.getString("login");
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }

       return "none";
   }

   public void setSession(String login, String session){
       try {
           checkConnection();

           String query = "UPDATE users SET `session` = ? WHERE `login` = ?";

           PreparedStatement ps = mysql.connection.prepareStatement(query);
           ps.setString(1, session);
           ps.setString(2, login);

           ps.execute();
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
   }

   public ResultSet getUsers(){
       try {
            checkConnection();

            String query = "SELECT * FROM users;";

            PreparedStatement ps = mysql.connection.prepareStatement(query);

            return ps.executeQuery();
       } catch (SQLException | ClassNotFoundException ex){
           ex.printStackTrace();
       }
       return null;
   }


}
