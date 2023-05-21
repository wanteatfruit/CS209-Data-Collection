package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static String TOKEN = "token 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";
    //TODO: change the path to your own path
    static String JDBC = "jdbc:sqlite:D:\\学习app\\CS209\\Final-proj\\data_collect\\CS209-Data-Collection\\database\\stackoverflow.db";
    private static Connection connection;

    public static Connection getInstance() {
        if(connection == null){
            try{
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(JDBC);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return connection;
    }
}