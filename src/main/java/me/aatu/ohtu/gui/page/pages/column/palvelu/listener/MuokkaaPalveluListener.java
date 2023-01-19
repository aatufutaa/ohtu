package me.aatu.ohtu.gui.page.pages.column.palvelu.listener;

import me.aatu.ohtu.gui.page.pages.column.listener.TuoteFieldsListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.column.palvelu.PalveluColumn;
import me.aatu.ohtu.database.table.Palvelu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MuokkaaPalveluListener extends TuoteFieldsListener<Palvelu> {

    public MuokkaaPalveluListener(PalveluColumn palveluColumn) {
        super(palveluColumn);
    }

    @Override
    public String getSendButtonName() {
        return "Tallenna";
    }

    @Override
    public String getOstikko() {
        return "Muokkaa palvelun tietoja";
    }

    @Override
    public String getStatus() {
        return "Tallennetaan palvelua...";
    }

    @Override
    public String insert() {
        Palvelu valittu = this.tuoteColumn.getValittuTuote();

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
                "update palvelu set nimi=?, kuvaus=?, hinta=? where palvelu_id=?",
                send.toArray()
        );

        return ok ? "Palvelu tallennettu tietokantaan." : "Palvelua ei voitu syöttää tietokantaan. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
    }

    @Override
    public void initField(int index, TextComponentField f) {
        super.initField(index, f);
        Palvelu valittu = this.tuoteColumn.getValittuTuote();
        f.setText(switch (index) {
            case 0 -> valittu.getNimi();
            case 1 -> valittu.getKuvaus();
            case 2 -> "" + valittu.getHinta();
            default -> "";
        });
    }
}
