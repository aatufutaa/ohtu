package me.aatu.ohtu.database.table;

import lombok.Getter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class Varaus {

    private int id, asiakasId, mokki_id;
    private Date varattuPvm, vahvistusPvm, varattuAlkuPvm, varattuLoppuPvm;

    public static Varaus readFromRS(ResultSet rs) throws SQLException {
        Varaus varaus = new Varaus();
        varaus.id = rs.getInt("varaus_id");
        varaus.asiakasId = rs.getInt("asiakas_id");
        varaus.mokki_id = rs.getInt("mokki_mokki_id");
        varaus.varattuPvm = rs.getDate("varattu_pvm");
        varaus.vahvistusPvm = rs.getDate("vahvistus_pvm");
        varaus.varattuAlkuPvm = rs.getDate("varattu_alkupvm");
        varaus.varattuLoppuPvm = rs.getDate("varattu_loppupvm");
        return varaus;
    }
}
