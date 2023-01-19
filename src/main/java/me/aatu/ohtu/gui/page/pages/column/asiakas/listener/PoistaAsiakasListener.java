package me.aatu.ohtu.gui.page.pages.column.asiakas.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.PoistaListener;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;

public class PoistaAsiakasListener extends PoistaListener<Asiakas> {

    public PoistaAsiakasListener(AsiakasColumn asiakasColumn) {
        super(asiakasColumn);
    }

    @Override
    public String getOstikko() {
        return "Poista asiakas";
    }

    @Override
    public String getStatus() {
        return "Poistetaan asiakasta...";
    }

    @Override
    public String insert() {
        boolean deleted = Program.getInstance().getDatabaseManager().update("delete from asiakas where asiakas_id=?", this.tuoteColumn.getValittuTuote().getId());
        return deleted ? "Asiakas poistettu tietokannasta" : Program.getInstance().getDatabaseManager().getLastError();
    }

    @Override
    public String getLog(Asiakas asiakas) {
        return "Haluatko varmasti poistaa asiakkaan " + asiakas.getEtunimi() + " " + asiakas.getSukunimi() + "?";
    }
}
