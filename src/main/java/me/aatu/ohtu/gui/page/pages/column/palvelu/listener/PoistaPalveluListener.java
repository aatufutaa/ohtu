package me.aatu.ohtu.gui.page.pages.column.palvelu.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.PoistaListener;
import me.aatu.ohtu.gui.page.pages.column.palvelu.PalveluColumn;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Palvelu;

public class PoistaPalveluListener extends PoistaListener<Palvelu> {

    public PoistaPalveluListener(PalveluColumn palveluColumn) {
        super(palveluColumn);
    }

    @Override
    public String getOstikko() {
        return "Poista palvelu";
    }

    @Override
    public String getLog(Palvelu tuote) {
        return "Haluatko varmasti poistaa palvelun " + tuote.getNimi() + "?";
    }

    @Override
    public String getStatus() {
        return "Poistetaan palvelua...";
    }

    @Override
    public String insert() {
        boolean deleted = Program.getInstance().getDatabaseManager().update("delete from palvelu where palvelu_id=?", this.tuoteColumn.getValittuTuote().getId());
        return deleted ? "Palvelu poistettu tietokannasta" : Program.getInstance().getDatabaseManager().getLastError();
    }
}
