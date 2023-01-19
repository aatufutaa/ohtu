package me.aatu.ohtu.database.table;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
public class Palvelu {

    private int id;
    private int alueId;
    private String nimi;
    private String kuvaus;
    private double hinta;

    public static Palvelu readFromRS(ResultSet rs) throws SQLException {
        Palvelu mokki = new Palvelu();
        mokki.id = rs.getInt("palvelu_id");
        mokki.alueId = rs.getInt("alue_id");
        mokki.nimi = rs.getString("nimi");
        mokki.kuvaus = rs.getString("kuvaus");
        mokki.hinta = rs.getDouble("hinta");
        return mokki;
    }
}
