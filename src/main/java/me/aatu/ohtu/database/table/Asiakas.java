package me.aatu.ohtu.database.table;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class Asiakas {

    private int id;
    private String postinro;
    private String etunimi;
    private String sukunimi;
    private String osoite;
    private String paikkakunta;
    private String email;
    private String puhelinnro;

    public static Asiakas readFromRS(ResultSet rs) throws SQLException {
        Asiakas mokki = new Asiakas();
        mokki.id = rs.getInt("asiakas_id");
        mokki.postinro = rs.getString("postinro");
        mokki.etunimi = rs.getString("etunimi");
        mokki.sukunimi = rs.getString("sukunimi");
        mokki.osoite = rs.getString("lahiosoite");
        mokki.paikkakunta = rs.getString("paikkakunta");
        mokki.email = rs.getString("email");
        mokki.puhelinnro = rs.getString("puhelinnro");
        return mokki;
    }

}
