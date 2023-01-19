package me.aatu.ohtu.gui.page.pages.column.mokki.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.TuoteFieldsListener;
import me.aatu.ohtu.gui.page.pages.column.mokki.MokkiColumn;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Mokki;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LisaaMokkiListener extends TuoteFieldsListener<Mokki> {

    public LisaaMokkiListener(MokkiColumn mokkiColumn) {
        super(mokkiColumn);
    }

    @Override
    public String getSendButtonName() {
        return "Lisää";
    }

    @Override
    public String getOstikko() {
        return "Syötä mökin tiedot";
    }

    @Override
    public String getStatus() {
        return "Lisätään mökkiä...";
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
                "insert into mokki (alue_id, postinro, mokkinimi, katuosoite, hinta, kuvaus, henkilomaara, varustelu) values (?,?,?,?,?,?,?,?)",
                send.toArray()
        );

        return ok ? "Mökki lisätty tietokantaan." : "Mökkiä ei voitu syöttää tietokantaan. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
    }
}
