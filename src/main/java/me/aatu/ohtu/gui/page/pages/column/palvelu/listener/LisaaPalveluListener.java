package me.aatu.ohtu.gui.page.pages.column.palvelu.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.TuoteFieldsListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.column.palvelu.PalveluColumn;
import me.aatu.ohtu.database.table.Palvelu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LisaaPalveluListener extends TuoteFieldsListener<Palvelu> {

    public LisaaPalveluListener(PalveluColumn palveluColumn) {
        super(palveluColumn);
    }

    @Override
    public String getSendButtonName() {
        return "Lisää";
    }

    @Override
    public String getOstikko() {
        return "Syötä palvelun tiedot";
    }

    @Override
    public String getStatus() {
        return "Lisätään palvelua...";
    }

    @Override
    public String insert() {
        List<Object> send = new ArrayList<>();
        send.add(Program.getInstance().getGui().getGuiOptions().getCurrentId());
        List<TextComponentField> list = new ArrayList<>(this.tuoteColumn.getTextComponentOptions());
        list.sort(Comparator.comparingInt(TextComponentField::getOrder));
        list.forEach(t -> send.add(switch (t.getType()) {
            case STRING -> t.getStr();
            case INTEGER -> t.getInt();
            case DOUBLE -> t.getDouble();
        }));

        boolean ok = Program.getInstance().getDatabaseManager().update(
                "insert into palvelu (alue_id, nimi, kuvaus, hinta) values (?,?,?,?)",
                send.toArray()
        );

        return ok ? "Palvelu lisätty tietokantaan." : "Palvelua ei voitu syöttää tietokantaan. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
    }
}
