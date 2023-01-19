package me.aatu.ohtu.gui.page.pages;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;
import me.aatu.ohtu.gui.page.pages.column.mokki.MokkiColumn;
import me.aatu.ohtu.gui.page.pages.column.palvelu.PalveluColumn;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.GuiPage;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;
import me.aatu.ohtu.database.table.Mokki;
import me.aatu.ohtu.database.table.Palvelu;

import javax.swing.*;
import java.awt.*;

public class HallintaSivu extends GuiPage {

    // mökit | asiakkaat | palvelut
    private final MokkiColumn mokkiColumn = new MokkiColumn(this);
    private final AsiakasColumn asiakasColumn = new AsiakasColumn(this);
    private final PalveluColumn palveluColumn = new PalveluColumn(this);

    @Override
    public JPanel getContentPanel() {
        this.mokkiColumn.hae();
        this.asiakasColumn.hae();
        this.palveluColumn.hae();

        JPanel title = GuiHelper.createVerticalPanel();
        JLabel otsikko = new JLabel("Mökkienhallintajärjestelmä");
        otsikko.setFont(new Font(otsikko.getFont().getName(), Font.BOLD, otsikko.getFont().getSize() + 4));
        title.add(GuiHelper.createExtraPanel(otsikko));
        JButton vaihdaAluetta = new JButton("Vaihda");
        vaihdaAluetta.setMargin(new Insets(1, 2, 1, 2));
        vaihdaAluetta.addActionListener(l -> Program.getInstance().getGui().showPage(new AlueenValintaPage()));
        JPanel tietojaPanel = GuiHelper.createHorizontalPanel();
        tietojaPanel.add(new JLabel("Valittu alue: " + Program.getInstance().getGui().getGuiOptions().getAlue()));
        tietojaPanel.add(vaihdaAluetta);
        title.add(tietojaPanel);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3));

        // Mökit
        panel.add(this.mokkiColumn.getPanel());

        // Asiakkaat
        panel.add(this.asiakasColumn.getPanel());

        // Palvelut
        panel.add(this.palveluColumn.getPanel());

        JPanel finalPanel = GuiHelper.createBorderPanel();
        finalPanel.add(GuiHelper.createExtraPanel(title), BorderLayout.PAGE_START); // Otsikko
        finalPanel.add(panel, BorderLayout.CENTER);

        return finalPanel;
    }

    public void onTuoteMuutettu(TuoteColumn<?> tuoteColumn) {
        this.asiakasColumn.onTuoteMuutettu(tuoteColumn);
    }

    public Mokki getValittuMokki() {
        return this.mokkiColumn.getValittuTuote();
    }

    public Palvelu getValittuPalvelu() {
        return this.palveluColumn.getValittuTuote();
    }

}
