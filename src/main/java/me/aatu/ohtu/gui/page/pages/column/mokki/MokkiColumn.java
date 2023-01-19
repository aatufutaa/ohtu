package me.aatu.ohtu.gui.page.pages.column.mokki;

import me.aatu.ohtu.gui.page.pages.column.mokki.listener.MuokkaaMokkiListener;
import me.aatu.ohtu.gui.page.pages.column.mokki.listener.PoistaMokkiListener;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.gui.textcomponent.TextComponentType;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.HallintaSivu;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;
import me.aatu.ohtu.gui.page.pages.column.mokki.listener.LisaaMokkiListener;
import me.aatu.ohtu.database.table.Mokki;

import java.util.*;

public class MokkiColumn extends TuoteColumn<Mokki> {

    public MokkiColumn(HallintaSivu hallintaSivu) {
        super(hallintaSivu);
    }

    @Override
    protected List<TextComponentField> getTextComponents() {
        return Arrays.asList(
                new TextComponentField("Mökin nimi", 45, false, TextComponentType.STRING, 1),
                new TextComponentField("Katuosoite", 45, false, TextComponentType.STRING, 2),
                new TextComponentField("Postinumero", 5, false, TextComponentType.INTEGER, 0),
                new TextComponentField("Hinta", 10, false, TextComponentType.DOUBLE, 3),
                new TextComponentField("Kuvaus", 150, true, TextComponentType.STRING, 4),
                new TextComponentField("Henkilömäärä", 3, false, TextComponentType.INTEGER, 5),
                new TextComponentField("Varustelu", 100, true, TextComponentType.STRING, 6));
    }

    @Override
    protected String[] getHakuTyypit() {
        return new String[] {"Nimi", "Id", "Hinta(max)"};
    }

    @Override
    protected String getTitle() {
        return "Mökit";
    }

    @Override
    protected Map<Integer, Mokki> haeData() {
        Map<Integer, Mokki> map = new HashMap<>();

        Program.getInstance().getDatabaseManager().query("select * from mokki where alue_id=" + Program.getInstance().getGui().getGuiOptions().getCurrentId(), (rs) -> {
            while (rs.next())
                map.put(rs.getInt("mokki_id"), Mokki.readFromRS(rs));
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
        if (suodatin != 1 || hakuKriteeri.length() == 0 || id != -1) {
            for (Mokki mokki : this.tuoteMap.values()) {
                if (hakuKriteeri.length() > 0) {

                    if (suodatin == 0) {
                        if (!mokki.getNimi().toLowerCase().contains(hakuKriteeri)) {
                            continue;
                        }
                    } else if (suodatin == 2) {
                        try {
                            double max = Double.parseDouble(hakuKriteeri);
                            if (mokki.getHinta() > max) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                        }
                    } else if (mokki.getId() != id) {
                        continue;
                    }
                }

                nimet.add("" + mokki.getId() + " | " + mokki.getNimi() + " | " + mokki.getHinta() + " € | " + mokki.getHenkilomaara() + " hlö");
            }
        }
        return nimet;
    }

    @Override
    protected void addListeners() {
        this.lisaa.addActionListener(new LisaaMokkiListener(this));
        this.muokkaa.addActionListener(new MuokkaaMokkiListener(this));
        this.poista.addActionListener(new PoistaMokkiListener(this));
    }
}
