package me.aatu.ohtu.gui.page.pages.column.asiakas;

import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.database.table.Mokki;
import me.aatu.ohtu.database.table.Palvelu;
import me.aatu.ohtu.gui.page.pages.HallintaSivu;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;
import me.aatu.ohtu.gui.page.pages.column.asiakas.listener.*;
import me.aatu.ohtu.gui.page.pages.column.mokki.MokkiColumn;
import me.aatu.ohtu.gui.page.pages.column.palvelu.PalveluColumn;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.gui.textcomponent.TextComponentType;

import java.util.*;

public class AsiakasColumn extends TuoteColumn<Asiakas> {

    private static final int NIMI_SUODATIN = 0;
    private static final int EMAIL_SUODATIN = 1;
    private static final int ID_SUODATIN = 2;

    public AsiakasColumn(HallintaSivu hallintaSivu) {
        super(hallintaSivu);
    }

    @Override
    protected List<TextComponentField> getTextComponents() {
        return Arrays.asList(
                new TextComponentField("Etunimi", 20, false, TextComponentType.STRING, 1),
                new TextComponentField("Sukunimi", 40, false, TextComponentType.STRING, 2),
                new TextComponentField("Osoite", 40, false, TextComponentType.STRING, 2),
                new TextComponentField("Paikkakunta", 40, false, TextComponentType.STRING, 2),
                new TextComponentField("Postinumero", 5, false, TextComponentType.INTEGER, 0),
                new TextComponentField("Email", 50, false, TextComponentType.STRING, 2),
                new TextComponentField("Puhelinnumero", 15, false, TextComponentType.STRING, 2)
        );
    }

    @Override
    protected String[] getHakuTyypit() {
        return new String[] {"Nimi", "Email", "Id"};
    }

    @Override
    protected String getTitle() {
        return "Asiakkaat";
    }

    @Override
    protected Map<Integer, Asiakas> haeData() {
        Map<Integer, Asiakas> map = new HashMap<>();

        Program.getInstance().getDatabaseManager().query("select * from asiakas", (rs) -> {
            while (rs.next())
                map.put(rs.getInt("asiakas_id"), Asiakas.readFromRS(rs));
        });

        return map;
    }

    @Override
    protected List<String> suodata(String hakuKriteeri, int suodatin) {
        int id = -1;
        if (suodatin == ID_SUODATIN) { // id
            try {
                id = Integer.parseInt(hakuKriteeri);
            } catch (NumberFormatException e) {
            }
        }

        java.util.List<String> nimet = new ArrayList<>();
        if (suodatin != ID_SUODATIN || hakuKriteeri.length() == 0 || id != -1) {
            for (Asiakas asiakas : this.tuoteMap.values()) {
                String nimi = asiakas.getEtunimi() + " " + asiakas.getSukunimi();
                if (hakuKriteeri.length() > 0) {
                    if (suodatin == NIMI_SUODATIN) {
                        if (!nimi.toLowerCase().contains(hakuKriteeri)) {
                            continue;
                        }
                    } else if (suodatin == EMAIL_SUODATIN) {
                        if (!asiakas.getEmail().toLowerCase().contains(hakuKriteeri)) {
                            continue;
                        }
                    } else if (asiakas.getId() != id)
                        continue;
                }

                nimet.add("" + asiakas.getId() + " | " + nimi);
            }
        }
        return nimet;
    }

    private VaraaListener varaaListener;

    @Override
    protected void addListeners() {
        this.lisaa.addActionListener(new LisaaAsiakasListener(this));
        this.muokkaa.addActionListener(new MuokkaaAsiakasListener(this));
        this.poista.addActionListener(new PoistaAsiakasListener(this));

        this.teeVaraus.addActionListener(this.varaaListener = new VaraaListener(this));
        this.varaukset.addActionListener(new VarauksetListener(this));
        this.laskut.addActionListener(new LaskuListener(this));
    }

    public void onTuoteMuutettu(TuoteColumn<?> tuoteColumn) {
        if (tuoteColumn instanceof MokkiColumn) {
            if (this.varaaListener != null)
                this.varaaListener.setValittuMokki((Mokki) tuoteColumn.getValittuTuote());
        } else if (tuoteColumn instanceof PalveluColumn) {
            if (this.varaaListener != null)
                this.varaaListener.setValittuPalvelu((Palvelu) tuoteColumn.getValittuTuote());
        }
    }
}
