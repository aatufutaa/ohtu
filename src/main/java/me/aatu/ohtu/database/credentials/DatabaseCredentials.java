package me.aatu.ohtu.database.credentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class DatabaseCredentials {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String CREDENTIALS_FILE = "credentials.json";

    @Getter private String database;
    @Getter private String host;
    @Getter private String username;
    @Getter private String password;
    @Getter private int port;

    @Getter private String lastError;

    public boolean read() {
        File credentials = new File(CREDENTIALS_FILE);
        if (!credentials.exists()) {
            try {
                credentials.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                this.lastError = e.getMessage();
                return false;
            }

            JsonObject defaultJson = new JsonObject();
            defaultJson.addProperty("database", "vn");
            defaultJson.addProperty("host", "localhost");
            defaultJson.addProperty("user", "root");
            defaultJson.addProperty("port", "3306");
            defaultJson.addProperty("password", "root");

            try (PrintWriter out = new PrintWriter(credentials)) {
                out.println(GSON.toJson(defaultJson));
            } catch (Exception e) {
                e.printStackTrace();
                this.lastError = e.getMessage();
                return false;
            }
        }

        String in;
        try {
            in = new String(Files.readAllBytes(credentials.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
            return false;
        }

        JsonObject json = GSON.fromJson(in, JsonObject.class);

        this.database = json.get("database").getAsString();
        this.host = json.get("host").getAsString();
        this.username = json.get("user").getAsString();
        this.port = json.get("port").getAsInt();
        this.password = json.get("password").getAsString();

        return true;
    }
}
