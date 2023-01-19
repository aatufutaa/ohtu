package me.aatu.ohtu.gui.page.pages.column.palvelu;

import me.aatu.ohtu.gui.page.pages.column.palvelu.listener.LisaaPalveluListener;
import me.aatu.ohtu.gui.page.pages.column.palvelu.listener.MuokkaaPalveluListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.gui.textcomponent.TextComponentType;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.HallintaSivu;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;
import me.aatu.ohtu.gui.page.pages.column.palvelu.listener.PoistaPalveluListener;
import me.aatu.ohtu.database.table.Palvelu;

import java.util.*;

public class PalveluColumn extends TuoteColumn<Palvelu> {

    public PalveluColumn(HallintaSivu hallintaSivu) {
        super(hallintaSivu);
    }

    @Override
    protected List<TextComponentField> getTextComponents() {
        return Arrays.asList(
                new TextComponentField("Palvelun nimi", 45, false, TextComponentType.STRING, 0),
                new TextComponentField("Kuvaus", 255, true, TextComponentType.STRING, 1),
                new TextComponentField("Hinta", 10, false, TextComponentType.DOUBLE, 2));
    }

    @Override
    protected String[] getHakuTyypit() {
        return new String[] { "Nimi", "Id" };
    }

    @Override
    protected String getTitle() {
        return "Palvelut";
    }

    @Override
    protected Map<Integer, Palvelu> haeData() {
        Map<Integer, Palvelu> map = new HashMap<>();

        Program.getInstance().getDatabaseManager().query("select * from palvelu where alue_id=" + Program.getInstance().getGui().getGuiOptions().getCurrentId(), (rs) -> {
            while (rs.next())
                map.put(rs.getInt("palvelu_id"), Palvelu.readFromRS(rs));
        });

        return map;
    }

    @Override
    protected List<String> suodata(String hakuKriteeri, int suodatin) {
        int id = -1;
        if (suodatin == 1) { // id
            try {
                id = Integer.parseInt(hakuKriteeri);
            } catch (NumberFormatException e) {
            }
        }

        java.util.List<String> nimet = new ArrayList<>();
        if (suodatin == 0 || hakuKriteeri.length() == 0 || id != -1) {
            for (Palvelu palvelu : this.tuoteMap.values()) {
                if (hakuKriteeri.length() > 0) {
                    if (suodatin == 0) {
                        if (!palvelu.getNimi().toLowerCase().contains(hakuKriteeri)) {
                            continue;
                        }
                    } else if (palvelu.getId() != id)
                        continue;
                }

                nimet.add("" + palvelu.getId() + " | " + palvelu.getNimi()
                        + " | " + palvelu.getHinta() + " € | ");
            }
        }
        return nimet;
    }

    @Override
    protected void addListeners() {
        this.lisaa.addActionListener(new LisaaPalveluListener(this));
        this.muokkaa.addActionListener(new MuokkaaPalveluListener(this));
        this.poista.addActionListener(new PoistaPalveluListener(this));
    }
}
