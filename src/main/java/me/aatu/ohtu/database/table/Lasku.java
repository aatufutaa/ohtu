package me.aatu.ohtu.database.table;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class Lasku {

    private int id, varausId;
    private double summa, alv;
    private boolean maksettu;

    public static Lasku readFromRS(ResultSet rs) throws SQLException {
        Lasku varaus = new Lasku();
        varaus.id = rs.getInt("lasku_id");
        varaus.varausId = rs.getInt("varaus_id");
        varaus.summa = rs.getDouble("summa");
        varaus.alv = rs.getDouble("alv");
        varaus.maksettu = rs.getBoolean("maksettu");
        return varaus;
    }
}
