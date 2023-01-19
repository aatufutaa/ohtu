package me.aatu.ohtu.gui.page.pages.column.asiakas.listener;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.page.pages.column.listener.TuoteButtonListener;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;
import me.aatu.ohtu.gui.page.pages.column.asiakas.calendar.DatePicker;
import me.aatu.ohtu.gui.page.pages.column.asiakas.calendar.Kalenteri;
import me.aatu.ohtu.database.table.Mokki;
import me.aatu.ohtu.database.table.Palvelu;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

public class VaraaListener extends TuoteButtonListener<Asiakas> {

    private JTextField valittuMokki;
    private JLabel mokkiVirhe;

    private JTextField valittuPalvelu;

    private DatePicker alkuDatePicker;
    private DatePicker loppuDatePicker;

    private final java.util.List<JTextField> palvelut = new ArrayList<>();

    public VaraaListener(AsiakasColumn tuoteColumn) {
        super(tuoteColumn);
    }

    @Override
    public JComponent addCustomComponents() {
        JPanel panel = GuiHelper.createVerticalPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        Asiakas valittu = this.tuoteColumn.getValittuTuote();
        JTextField valittuAsiakasField = GuiHelper.createFieldWithText("Valittu asiakas:", panel, null);
        valittuAsiakasField.setText(valittu.getEtunimi() + " " + valittu.getSukunimi());

        JLabel mokkiError = GuiHelper.createErrorLabel();
        this.mokkiVirhe = mokkiError;
        panel.add(mokkiError);
        JTextField valittuMokkiField = GuiHelper.createFieldWithText("Valittu mökki: (valitse mökki listasta)", panel, null);
        this.valittuMokki = valittuMokkiField;

        JPanel palvelut = GuiHelper.createVerticalPanel();
        panel.add(palvelut);

        JPanel lisaaUusiPanel = GuiHelper.createBorderPanel();
        JButton lisaaUusi = new JButton("Lisää palvelu");
        lisaaUusi.addActionListener(l -> {
            JPanel palveluPanel = GuiHelper.createVerticalPanel();
            JButton poistaPalvelu = new JButton("Pois");
            JTextField valittuPalveluField = GuiHelper.createFieldWithText("Valittu palvelu: (valitse palvelu listasta)", palveluPanel, poistaPalvelu);
            this.valittuPalvelu = valittuPalveluField;
            Palvelu valittuPalvelu = this.tuoteColumn.hallintaSivu.getValittuPalvelu();
            this.setValittuPalvelu(valittuPalvelu);
            poistaPalvelu.addActionListener(l2 -> {
                palvelut.remove(palveluPanel);
                this.palvelut.remove(valittuPalveluField);
                Program.getInstance().getGui().refresh();
            });
            palvelut.add(palveluPanel);
            this.palvelut.add(valittuPalveluField);
            Program.getInstance().getGui().refresh();
        });
        lisaaUusiPanel.add(lisaaUusi, BorderLayout.LINE_END);
        panel.add(lisaaUusiPanel);

        this.alkuDatePicker = new DatePicker("Alku pvm", this);
        panel.add(this.alkuDatePicker.getPanel());

        this.loppuDatePicker = new DatePicker("Loppu pvm", this);
        panel.add(this.loppuDatePicker.getPanel());

        this.setValittuMokki(this.tuoteColumn.hallintaSivu.getValittuMokki());

        JPanel textFieldPanel = GuiHelper.createBorderPanel();
        textFieldPanel.add(panel, BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textFieldPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    public void setValittuMokki(Mokki mokki) {
        if (this.valittuMokki == null)
            return;
        if (mokki == null) {
            this.valittuMokki.setText("ei valittu");
        } else {
            this.valittuMokki.setText(mokki.getNimi());
        }
        this.updateKalenteriColors(mokki, this.alkuDatePicker.getKalenteri());
        this.updateKalenteriColors(mokki, this.loppuDatePicker.getKalenteri());
    }

    public void setValittuPalvelu(Palvelu palvelu) {
        if (this.valittuPalvelu == null)
            return;
        if (palvelu == null) {
            this.valittuPalvelu.setText("ei valittu");
        } else {
            this.valittuPalvelu.setText(palvelu.getId() + " | " + palvelu.getNimi());
        }
    }

    public void updateKalenteriColors(Kalenteri kalenteri) {
        this.updateKalenteriColors(this.tuoteColumn.hallintaSivu.getValittuMokki(), kalenteri);
    }

    private void updateKalenteriColors(Mokki valittu, Kalenteri kalenteri) {
        kalenteri.reset();

        if (valittu == null) {
            return;
        }

        int year = kalenteri.getYear();
        int month = kalenteri.getMonth() + 1;

        String statement = "select * from varaus where mokki_mokki_id=" + valittu.getId() + " and "
                + "year(varattu_alkupvm) >= " + year + " and " + "year(varattu_loppupvm) >= " + year
                + " and month(varattu_alkupvm) >= " + month + " and " + "month(varattu_loppupvm) >=" + month;

        Program.getInstance().getDatabaseManager().query(statement, rs -> {
            while (rs.next()) {

                Date start = rs.getDate("varattu_alkupvm");
                Date end = rs.getDate("varattu_loppupvm");

                int startDay = getStartDay(start);
                int endDay = getStartDay(end);

                for (int day = startDay; day < endDay + 1; day++) {
                    kalenteri.setColor(day, Color.RED);
                }
            }
        });
    }

    public static int getStartDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    @Override
    public boolean test() {
        boolean virhe = false;
        if (this.tuoteColumn.hallintaSivu.getValittuMokki() == null) {
            this.mokkiVirhe.setText("Mökkiä ei valittu");
            virhe = true;
        } else {
            this.mokkiVirhe.setText("");
        }
        if (!this.alkuDatePicker.test() || !this.loppuDatePicker.test()) {
            virhe = true;
        } else if (this.alkuDatePicker.getSelectedDate().getTime() > this.loppuDatePicker.getSelectedDate().getTime()) {
            virhe = true;
            this.loppuDatePicker.setError("Loppupvm on ennen alkupvm");
        }
        return virhe;
    }

    private Timestamp getAlkuPvm() {
        return new Timestamp(this.alkuDatePicker.getSelectedDate().getTime());

    }

    private Timestamp getLoppuPvm() {
        return new Timestamp(this.loppuDatePicker.getSelectedDate().getTime());
    }

    @Override
    public String getOstikko() {
        return "Varaa mökki";
    }

    @Override
    public String getSendButtonName() {
        return "Varaa";
    }

    @Override
    public String getStatus() {
        return "Varataan mökkiä...";
    }

    @Override
    public String insert() {

        String statement = "insert into varaus (asiakas_id, mokki_mokki_id, varattu_pvm, varattu_alkupvm, varattu_loppupvm) values"
                + " (?,?,?,?,?)";

        Asiakas asiakas = this.tuoteColumn.getValittuTuote();

        int asiakasId = asiakas.getId();
        int mokkiId = this.tuoteColumn.hallintaSivu.getValittuMokki().getId();

        java.sql.Timestamp varattuPvm = new java.sql.Timestamp(System.currentTimeMillis());

        Timestamp alkuPvm = this.getAlkuPvm();
        Timestamp loppuPvm = this.getLoppuPvm();

        boolean ok = Program.getInstance().getDatabaseManager().update(statement,
                asiakasId,
                mokkiId,
                varattuPvm,
                alkuPvm,
                loppuPvm);

        if (!ok) {
            return "Varaus epäonnistui. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
        }

        if (this.palvelut.size() > 0) {
            statement = "insert into varauksen_palvelut (varaus_id, palvelu_id, lkm) values (LAST_INSERT_ID(),?,?)";
            Set<Integer> palvelut = new HashSet<>();
            Map<Integer, Integer> maarat = new HashMap<>();
            for (JTextField t : this.palvelut) {
                String text = t.getText();
                if (!text.contains("|")) // ei valittu palvelu
                    continue;
                int id = Integer.parseInt(text.split(" ")[0]);
                if (palvelut.contains(id)) {
                    maarat.put(id, maarat.get(id) + 1);
                    continue;
                }
                palvelut.add(id);
                maarat.put(id, 1);
            }
            for (Map.Entry<Integer, Integer> e : maarat.entrySet()) {
                ok = Program.getInstance().getDatabaseManager().update(statement, e.getKey(), e.getValue());
                if (!ok) {
                    return "Palvelua ei voitu syöttää. Virhe: " + Program.getInstance().getDatabaseManager().getLastError();
                }
            }
        }

        return "Varaus onnistui";
    }
}
