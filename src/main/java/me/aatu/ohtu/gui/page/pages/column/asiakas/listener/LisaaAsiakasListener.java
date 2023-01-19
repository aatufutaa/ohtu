package me.aatu.ohtu.gui.page.pages.column.asiakas.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.TuoteFieldsListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LisaaAsiakasListener extends TuoteFieldsListener<Asiakas> {

    public LisaaAsiakasListener(AsiakasColumn asiakasColumn) {
        super(asiakasColumn);
    }

    @Override
    public String getSendButtonName() {
        return "Lisää";
    }

    @Override
    public String getOstikko() {
        return "Syötä asiakkaan tiedot";
    }

    @Override
    public String getStatus() {
        return "Lisätään asiakasta...";
    }

    @Override
    public String insert() {
        List<Object> send = new ArrayList<>();
        List<TextComponentField> list = new ArrayList<>(this.tuoteColumn.getTextComponentOptions());
        list.sort(Comparator.comparingInt(TextComponentField::getOrder));
        list.forEach(t -> send.add(switch (t.getType()) {
            case STRING -> t.getStr();
            case INTEGER -> t.getInt();
            case DOUBLE -> t.getDouble();
        }));

        boolean ok = Program.getInstance().getDatabaseManager().update(
                "insert into asiakas (postinro, etunimi, sukunimi, lahiosoite, paikkakunta, email, puhelinnro) values (?,?,?,?,?,?,?)",
                send.toArray()
        );

        return ok ? "Asiakas lisätty tietokantaan." : "Asiakasta ei voitu syöttää tietokantaan. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
    }
}
