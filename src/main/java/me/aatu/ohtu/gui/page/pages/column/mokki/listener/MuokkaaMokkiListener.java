package me.aatu.ohtu.gui.page.pages.column.mokki.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.TuoteFieldsListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.column.mokki.MokkiColumn;
import me.aatu.ohtu.database.table.Mokki;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MuokkaaMokkiListener extends TuoteFieldsListener<Mokki> {

    public MuokkaaMokkiListener(MokkiColumn mokkiColumn) {
        super(mokkiColumn);
    }

    @Override
    public String getSendButtonName() {
        return "Tallenna";
    }

    @Override
    public String getOstikko() {
        return "Muokkaa mökin tietoja";
    }

    @Override
    public String getStatus() {
        return "Tallennetaan mökkiä...";
    }

    @Override
    public String insert() {
        Mokki valittu = this.tuoteColumn.getValittuTuote();

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
                "update mokki set postinro=?, mokkinimi=?, katuosoite=?, hinta=?, kuvaus=?, henkilomaara=?, varustelu=? where mokki_id=?",
                send.toArray());

        return ok ? "Mökki tallennettu tietokantaan." : "Mökkiä ei voitu syöttää tietokantaan. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
    }

    @Override
    public void initField(int index, TextComponentField f) {
        super.initField(index, f);
        Mokki valittu = this.tuoteColumn.getValittuTuote();
        f.setText(switch (index) {
            case 0 -> valittu.getNimi();
            case 1 -> valittu.getKatuosoite();
            case 2 -> valittu.getPostinumero();
            case 3 -> "" + valittu.getHinta();
            case 4 -> valittu.getKuvaus();
            case 5 -> "" + valittu.getHenkilomaara();
            case 6 -> valittu.getVarustelu();
            default -> "";
        });
    }
}
