package me.aatu.ohtu.database.callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RSCallback {

    void call(ResultSet rs) throws SQLException;

}
