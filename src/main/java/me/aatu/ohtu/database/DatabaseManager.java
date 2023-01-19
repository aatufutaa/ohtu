package me.aatu.ohtu.database;

import me.aatu.ohtu.database.callback.RSCallback;
import me.aatu.ohtu.database.credentials.DatabaseCredentials;
import me.aatu.ohtu.gui.page.pages.AlueenValintaPage;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.driver.SQLDriverLoader;

import java.sql.*;

public class DatabaseManager {

    private Connection connection;
    private String lastError;

    public DatabaseManager() {
    }

    public void start() {
        DatabaseCredentials credentials = new DatabaseCredentials();
        boolean success = credentials.read();
        if (!success) {
            Program.getInstance().getGui().showMsg("Virhe kirjautumistietojen lataamisessa! " + credentials.getLastError());
            return;
        }

        if (!SQLDriverLoader.loadDriver()) {
            Program.getInstance().getGui().showMsg("SQL ajuria ei voitu lataa");
            return;
        }

        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabase(),
                    credentials.getUsername(), credentials.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            Program.getInstance().getGui().showMsg("Yhteyttä tietokantaan ei voitu luoda. Tarkista kirjautumis tiedot tiedostosta "
                    + DatabaseCredentials.CREDENTIALS_FILE);
            return;
        }

        Program.getInstance().getGui().showPage(new AlueenValintaPage());
    }

    public void stop() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void query(String query, RSCallback callback) {
        Statement statement;
        ResultSet rs;

        try {
            statement = this.connection.createStatement();
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            callback.call(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean update(String update, Object... values) {
        PreparedStatement ps = null;
        boolean err = false;
        try {
            ps = this.connection.prepareStatement(update);

            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
            err = true;
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                err = true;
            }
        }
        return !err;
    }

    public String getLastError() {
        return this.lastError;
    }
}
