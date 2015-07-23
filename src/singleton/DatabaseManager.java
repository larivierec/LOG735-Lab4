package singleton;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static Connection instance = null;

    private DatabaseManager(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getInstance() throws SQLException{
        if(instance == null){
            new DatabaseManager();
        }
        try {
            instance = DriverManager.getConnection("jdbc:mysql://db.scanetworks.com/scanet_loginDatabase?user=scanet_log735&password=log735");
            //instance = DriverManager.getConnection("jdbc:mysql://localhost/scanet_loginDatabase?user=root");
        }catch(Exception e){
            e.printStackTrace();
        }
        return instance;
    }
}
