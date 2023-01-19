package me.aatu.ohtu.gui.page.pages.column.asiakas.listener;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.textcomponent.TextComponentHelper;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.database.table.Asiakas;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;
import me.aatu.ohtu.database.table.Lasku;
import me.aatu.ohtu.database.table.Varaus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LaskuListener implements ActionListener {

    protected final AsiakasColumn tuoteColumn;
    private final List<Lasku> varaukset = new ArrayList<>();

    private JScrollPane scrollList;
    private Lasku valittuVaraus;
    private JButton poista;
    private JButton vahvista;

    public LaskuListener(AsiakasColumn tuoteColumn) {
        this.tuoteColumn = tuoteColumn;
    }

    private void haeVaraukset() {
        this.varaukset.clear();
        List<String> varaukset = new ArrayList<>();
        String statement = "select * from lasku join varaus on lasku.varaus_id=varaus.varaus_id where asiakas_id=" + this.tuoteColumn.getValittuTuote().getId();
        Program.getInstance().getDatabaseManager().query(statement, rs -> {
            while (rs.next()) {
                Lasku varaus = Lasku.readFromRS(rs);
                varaukset.add(varaus.getId() + " | " + varaus.getSumma() + "e" + " | " + (varaus.isMaksettu() ? "maksettu" : "ei maksettu"));
                this.varaukset.add(varaus);
            }
        });
        JList<String> list = new JList<>(varaukset.toArray(String[]::new));
        list.addListSelectionListener(l -> {
            this.valittuVaraus = this.varaukset.get(list.getSelectedIndex());
            this.poista.setEnabled(!this.valittuVaraus.isMaksettu());
            this.vahvista.setEnabled(true);
        });
        this.valittuVaraus = null;
        this.scrollList.setViewportView(list);
        this.poista.setEnabled(false);
        this.vahvista.setEnabled(false);
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

        JButton poista = new JButton("Maksettu");
        poista.addActionListener(l -> {
            Lasku varaus = this.valittuVaraus;
            String statement = "update lasku set maksettu=true where lasku_id=?";

            otsikko.setText("Lasku merkitty maksetuksi");

            boolean ok = Program.getInstance().getDatabaseManager().update(statement, varaus.getId());
            String result = ok ? "Lasku merkitty maksetuksi" : "Lasku epäonnistui Virhe " + Program.getInstance().getDatabaseManager().getLastError();

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

        JButton vahvista = new JButton("Luo PDF");
        vahvista.addActionListener(l -> {
            otsikko.setText("Luodaan PDF");

            AtomicReference<Double> summa = new AtomicReference<>((double) 0);

            Lasku lasku = this.valittuVaraus;
            int varausId = lasku.getVarausId();

            AtomicReference<Varaus> varaus1 = new AtomicReference<>();

            String statement = "select * from varaus where varaus_id=" + varausId;
            Program.getInstance().getDatabaseManager().query(statement, rs -> {
                while (rs.next()) {
                    varaus1.set(Varaus.readFromRS(rs));
                }
            });

            Varaus varaus = varaus1.get();
            StringBuilder laskuSb = new StringBuilder();

            laskuSb.append("Varattu pvm: ").append(varaus.getVarattuPvm()).append("\n");
            laskuSb.append("Varattu alkupvm: ").append(varaus.getVarattuAlkuPvm()).append("\n");
            laskuSb.append("Varattu loppupvm: ").append(varaus.getVarattuLoppuPvm()).append("\n");

            statement = "select mokkinimi,hinta from mokki where mokki_id=" + varaus.getMokki_id();
            Program.getInstance().getDatabaseManager().query(statement, rs -> {
                while (rs.next()) {
                    laskuSb.append("Varattu mökki: ").append(rs.getString("mokkinimi")).append("\n");
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

                        laskuSb.append(rs.getString("nimi")).append(": ").append("x").append(e.getValue()).append("\n");
                        laskuSb.append("Hinta: ").append(h1).append(" x ").append(e.getValue()).append("e, yht. ").append(h).append("e").append("\n");
                    }
                });
            }
            if (palvelut.size() == 0)
                laskuSb.append("Ei palveluita").append("\n");

            laskuSb.append("yht. ").append(summa).append("e");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Valitse laskun sijainti");
            fileChooser.setCurrentDirectory(new File("./"));
            fileChooser.setSelectedFile(new File("./lasku_id" + lasku.getId() + ".pdf"));

            int select = fileChooser.showSaveDialog(finalPanel);

            if (select == JFileChooser.APPROVE_OPTION) {
                try {

                    PDDocument doc = null;
                    try {
                        doc = new PDDocument();
                        PDPage page = new PDPage();
                        doc.addPage(page);
                        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

                        PDFont pdfFont = PDType1Font.HELVETICA;
                        float fontSize = 25;
                        float leading = 1.5f * fontSize;

                        PDRectangle mediabox = page.getMediaBox();
                        float margin = 72;
                        float startX = mediabox.getLowerLeftX() + margin;
                        float startY = mediabox.getUpperRightY() - margin;

                        contentStream.beginText();
                        contentStream.setFont(pdfFont, fontSize);
                        contentStream.newLineAtOffset(startX, startY);

                        for (String line : laskuSb.toString().split("\n")) {
                            contentStream.showText(line);
                            contentStream.newLineAtOffset(0, -leading);
                        }

                        contentStream.endText();
                        contentStream.close();

                        doc.save(fileChooser.getSelectedFile());
                    } finally {
                        if (doc != null) {
                            doc.close();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        peruutaJaLisaa.add(vahvista);
        this.vahvista = vahvista;

        finalPanel.add(peruutaJaLisaa, BorderLayout.PAGE_END);

        this.tuoteColumn.getBorder().add(finalPanel, BorderLayout.CENTER);

        this.haeVaraukset();

        Program.getInstance().getGui().refresh();
    }
}
