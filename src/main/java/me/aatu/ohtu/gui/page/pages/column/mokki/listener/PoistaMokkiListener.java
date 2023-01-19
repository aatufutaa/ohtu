package me.aatu.ohtu.gui.page.pages.column.mokki.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.PoistaListener;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.column.mokki.MokkiColumn;
import me.aatu.ohtu.database.table.Mokki;

public class PoistaMokkiListener extends PoistaListener<Mokki> {

    public PoistaMokkiListener(MokkiColumn mokkiColumn) {
        super(mokkiColumn);
    }

    @Override
    public String getOstikko() {
        return "Poista mökki";
    }

    @Override
    public String getLog(Mokki tuote) {
        return "Haluatko varmasti poistaa mökin " + tuote.getNimi() + "?";
    }

    @Override
    public String getStatus() {
        return "Poistetaan mökkiä...";
    }

    @Override
    public String insert() {
        boolean deleted = Program.getInstance().getDatabaseManager().update("delete from mokki where mokki_id=?", this.tuoteColumn.getValittuTuote().getId());
        return deleted ? "Mökki poistettu tietokannasta" : Program.getInstance().getDatabaseManager().getLastError();
    }
}
