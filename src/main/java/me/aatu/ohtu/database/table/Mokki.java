package me.aatu.ohtu.database.table;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
public class Mokki {

    private int id;
    private int alueId;
    private String postinumero;
    private String nimi;
    private String katuosoite;
    private double hinta;
    private String kuvaus;
    private int henkilomaara;
    private String varustelu;

    public static Mokki readFromRS(ResultSet rs) throws SQLException {
        Mokki mokki = new Mokki();
        mokki.id = rs.getInt("mokki_id");
        mokki.alueId = rs.getInt("alue_id");
        mokki.postinumero = rs.getString("postinro");
        mokki.nimi = rs.getString("mokkinimi");
        mokki.katuosoite = rs.getString("katuosoite");
        mokki.hinta = rs.getDouble("hinta");
        mokki.kuvaus = rs.getString("kuvaus");
        mokki.henkilomaara = rs.getInt("henkilomaara");
        mokki.varustelu = rs.getString("varustelu");
        return mokki;
    }
}
