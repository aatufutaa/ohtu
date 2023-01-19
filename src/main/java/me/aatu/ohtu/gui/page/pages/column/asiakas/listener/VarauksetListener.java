package me.aatu.ohtu.gui.page.pages.column.asiakas.listener;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.textcomponent.TextComponentHelper;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;
import me.aatu.ohtu.database.table.Varaus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class VarauksetListener implements ActionListener {

    protected final AsiakasColumn tuoteColumn;
    private final List<Varaus> varaukset = new ArrayList<>();

    private JScrollPane scrollList;
    private Varaus valittuVaraus;
    private JButton poista;
    private JButton vahvista;

    public VarauksetListener(AsiakasColumn tuoteColumn) {
        this.tuoteColumn = tuoteColumn;
    }

    private void haeVaraukset() {
        this.varaukset.clear();
        List<String> varaukset = new ArrayList<>();
        String statement = "select * from varaus where asiakas_id=" + this.tuoteColumn.getValittuTuote().getId();
        Program.getInstance().getDatabaseManager().query(statement, rs -> {
            while (rs.next()) {
                Varaus varaus = Varaus.readFromRS(rs);
                varaukset.add(varaus.getId() + " | " + varaus.getVarattuPvm() + " | " + (varaus.getVahvistusPvm() != null ? "vahvistettu " + varaus.getVahvistusPvm() : "vahvistamaton"));
                this.varaukset.add(varaus);
            }
        });
        JList<String> list = new JList<>(varaukset.toArray(String[]::new));
        list.addListSelectionListener(l -> {
            this.valittuVaraus = this.varaukset.get(list.getSelectedIndex());
            if (this.valittuVaraus.getVahvistusPvm() != null) {
                this.valittuVaraus = null;
                return;
            }
            this.poista.setEnabled(true);
            this.vahvista.setEnabled(true);
        });
        this.valittuVaraus = null;
        this.scrollList.setViewportView(list);
        this.poista.setEnabled(false);
        this.vahvista.setEnabled(false);
    }

    public String insert() {
        Varaus varaus = this.valittuVaraus;

        StringBuilder laskuSb = new StringBuilder();

        String statement = "update varaus set vahvistus_pvm=? where varaus_id=?";
        Timestamp vahvistusTime = new Timestamp(System.currentTimeMillis());
        boolean ok = Program.getInstance().getDatabaseManager().update(statement, vahvistusTime, varaus.getId());
        if (!ok)
            return "Vahvistus virhe " + Program.getInstance().getDatabaseManager().getLastError();

        AtomicReference<Double> summa = new AtomicReference<>((double) 0);

        statement = "select mokkinimi,hinta from mokki where mokki_id=" + varaus.getMokki_id();
        Program.getInstance().getDatabaseManager().query(statement, rs -> {
            while (rs.next()) {
                laskuSb.append("Varattu mökki: ").append(rs.getString("mokkinimi")).append(" (id=").append(varaus.getMokki_id()).append(")").append("\n");
                java.util.Date start = varaus.getVarattuAlkuPvm();
                Date end = varaus.getVarattuLoppuPvm();
                long diff = end.getTime() - start.getTime();
                int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
                laskuSb.append("Yö määrä: ").append(days).append("\n");
                double h1 = rs.getDouble("hinta");
                double h = h1 * days;
                summa.updateAndGet(v -> v + h);
                laskuSb.append("Mökin hinta: ").append(h1).append("e x ").append(days).append(", yht. ").append(h).append("e").append("\n");
            }
        });

        laskuSb.append("Palvelut:").append("\n");
        Map<Integer, Integer> palvelut = new HashMap<>();
        statement = "select * from varauksen_palvelut where varaus_id=" + varaus.getId();
        Program.getInstance().getDatabaseManager().query(statement, rs -> {
            while (rs.next()) {
                int palveluId = rs.getInt("palvelu_id");
                int lkm = rs.getInt("lkm");
                palvelut.put(palveluId, lkm);
            }
        });

        for (Map.Entry<Integer, Integer> e : palvelut.entrySet()) {
            statement = "select nimi,hinta from palvelu where palvelu_id=" + e.getKey();
            Program.getInstance().getDatabaseManager().query(statement, rs -> {
                while (rs.next()) {
                    double h1 = rs.getDouble("hinta");
                    double h = h1 * e.getValue();
                    summa.updateAndGet(v -> v + h);

                    laskuSb.append(rs.getString("nimi")).append(" (id=").append(e.getKey()).append("): ").append("x").append(e.getValue()).append("\n");
                    laskuSb.append("Hinta: ").append(h1).append(" x ").append(e.getValue()).append("e, yht. ").append(h).append("e").append("\n");
                }
            });
        }
        if (palvelut.size() == 0)
            laskuSb.append("Ei palveluita").append("\n");

        statement = "insert into lasku (varaus_id,summa,alv) values (?,?,?)";
        Program.getInstance().getDatabaseManager().update(statement, varaus.getId(), summa.get(), 24);

        laskuSb.append("yht. ").append(summa).append("e");

        return "Vahvistus onnistui" + "\nLaskun tiedot:\n" + laskuSb;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.tuoteColumn.getBorder().remove(this.tuoteColumn.getTuotteetPanel());

        JPanel otsikkoPanel = GuiHelper.createHorizontalPanel();
        Asiakas asiakas = this.tuoteColumn.getValittuTuote();
        JLabel otsikko = new JLabel(asiakas.getEtunimi() + " " + asiakas.getSukunimi() + " Varaukset");
        otsikkoPanel.add(otsikko);

        JPanel finalPanel = GuiHelper.createBorderPanel();
        finalPanel.add(GuiHelper.createExtraPanel(otsikkoPanel), BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.scrollList = scrollPane;
        finalPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel peruutaJaLisaa = new JPanel();

        peruutaJaLisaa.add(GuiHelper.createButton("Peruuta", l1 -> {
            this.tuoteColumn.getBorder().remove(finalPanel);
            this.tuoteColumn.getBorder().add(this.tuoteColumn.getTuotteetPanel());
            Program.getInstance().getGui().refresh();
        }));

        JButton poista = new JButton("Poista");
        poista.addActionListener(l -> {
            Varaus varaus = this.valittuVaraus;

            String palvelutPois = "delete from varauksen_palvelut where varaus_id=?";
            Program.getInstance().getDatabaseManager().update(palvelutPois, varaus.getId());

            String statement = "delete from varaus where varaus_id=?";

            otsikko.setText("Varaus vahvistettu");

            boolean ok = Program.getInstance().getDatabaseManager().update(statement, varaus.getId());

            String result = ok ? "Varaus peruutettu" : "Peruutus epäonnistui Virhe " + Program.getInstance().getDatabaseManager().getLastError();

            finalPanel.remove(peruutaJaLisaa);
            finalPanel.remove(scrollPane);

            JTextArea virheLog = TextComponentHelper.createFixedTextArea();
            virheLog.setText(result);
            virheLog.setEditable(false);
            virheLog.setLineWrap(true);
            finalPanel.add(virheLog, BorderLayout.CENTER);

            JButton valmis = new JButton("Valmis");

            valmis.addActionListener(l2 -> {
                this.haeVaraukset();

                finalPanel.remove(valmis);
                finalPanel.remove(virheLog);

                finalPanel.add(scrollPane, BorderLayout.CENTER);
                finalPanel.add(peruutaJaLisaa, BorderLayout.PAGE_END);

                Program.getInstance().getGui().refresh();
            });

            finalPanel.add(valmis, BorderLayout.PAGE_END);

            Program.getInstance().getGui().refresh();
        });
        peruutaJaLisaa.add(poista);
        this.poista = poista;

        JButton vahvista = new JButton("Vahvista");
        vahvista.addActionListener(l -> {
            otsikko.setText("Varaus vahvistettu");

            String result = this.insert();

            finalPanel.remove(peruutaJaLisaa);
            finalPanel.remove(scrollPane);

            JTextArea virheLog = TextComponentHelper.createFixedTextArea();
            virheLog.setText(result);
            virheLog.setEditable(false);
            virheLog.setLineWrap(true);
            finalPanel.add(virheLog, BorderLayout.CENTER);

            JButton valmis = new JButton("Valmis");

            valmis.addActionListener(l2 -> {
                this.haeVaraukset();

                finalPanel.remove(valmis);
                finalPanel.remove(virheLog);

                finalPanel.add(scrollPane, BorderLayout.CENTER);
                finalPanel.add(peruutaJaLisaa, BorderLayout.PAGE_END);

                Program.getInstance().getGui().refresh();
            });

            finalPanel.add(valmis, BorderLayout.PAGE_END);

            Program.getInstance().getGui().refresh();
        });
        peruutaJaLisaa.add(vahvista);
        this.vahvista = vahvista;

        finalPanel.add(peruutaJaLisaa, BorderLayout.PAGE_END);

        this.tuoteColumn.getBorder().add(finalPanel, BorderLayout.CENTER);

        this.haeVaraukset();

        Program.getInstance().getGui().refresh();
    }
}
