package me.aatu.ohtu.gui.page.pages.column.asiakas.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.TuoteFieldsListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MuokkaaAsiakasListener extends TuoteFieldsListener<Asiakas> {

    public MuokkaaAsiakasListener(AsiakasColumn asiakasColumn) {
        super(asiakasColumn);
    }

    @Override
    public String getSendButtonName() {
        return "Tallenna";
    }

    @Override
    public String getOstikko() {
        return "Muokkaa asiakkaan tietoja";
    }

    @Override
    public String getStatus() {
        return "Tallennetaan asiakasta...";
    }

    @Override
    public String insert() {
        Asiakas valittu = this.tuoteColumn.getValittuTuote();

        List<Object> send = new ArrayList<>();
        List<TextComponentField> list = new ArrayList<>(this.tuoteColumn.getTextComponentOptions());
        list.sort(Comparator.comparingInt(TextComponentField::getOrder));
        list.forEach(t -> send.add(switch (t.getType()) {
            case STRING -> t.getStr();
            case INTEGER -> t.getInt();
            case DOUBLE -> t.getDouble();
        }));
        send.add(valittu.getId());

        boolean ok = Program.getInstance().getDatabaseManager().update(
                "update asiakas set postinro=?, etunimi=?, sukunimi=?, lahiosoite=?, paikkakunta=?, email=?, puhelinnro=? where asiakas_id=?",
                send.toArray());

        return ok ? "Asiakas tallennettu tietokantaan." : "Asiakasta ei voitu syöttää tietokantaan. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
    }

    @Override
    public void initField(int index, TextComponentField f) {
        super.initField(index, f);
        Asiakas valittu = this.tuoteColumn.getValittuTuote();
        f.setText(switch (index) {
            case 0 -> valittu.getEtunimi();
            case 1 -> valittu.getSukunimi();
            case 2 -> valittu.getOsoite();
            case 3 -> valittu.getPaikkakunta();
            case 4 -> valittu.getPostinro();
            case 5 -> valittu.getEmail();
            case 6 -> valittu.getPuhelinnro();
            default -> "";
        });
    }
}
