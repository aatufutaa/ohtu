package me.aatu.ohtu.database.driver;

public class SQLDriverLoader {

    public static boolean loadDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
