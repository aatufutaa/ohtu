package me.aatu.ohtu.gui.page.pages.column.listener;

import lombok.AllArgsConstructor;
import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.textcomponent.TextComponentHelper;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@AllArgsConstructor
public abstract class TuoteButtonListener<T> implements ActionListener {

    protected final TuoteColumn<T> tuoteColumn;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.tuoteColumn.getBorder().remove(this.tuoteColumn.getTuotteetPanel());

        JPanel otsikkoPanel = GuiHelper.createHorizontalPanel();
        JLabel otsikko = new JLabel(this.getOstikko());
        JLabel virhe = GuiHelper.createErrorLabel();
        otsikkoPanel.add(otsikko);
        otsikkoPanel.add(virhe);

        JPanel finalPanel = GuiHelper.createBorderPanel();
        finalPanel.add(GuiHelper.createExtraPanel(otsikkoPanel), BorderLayout.PAGE_START);

        JComponent custom = this.addCustomComponents();
        if (custom != null)
            finalPanel.add(custom, BorderLayout.CENTER);

        JPanel peruutaJaLisaa = new JPanel();
        JButton lisaa = new JButton(this.getSendButtonName());

        peruutaJaLisaa.add(GuiHelper.createButton("Peruuta", l1 -> {
            this.tuoteColumn.getBorder().remove(finalPanel);
            this.tuoteColumn.getBorder().add(this.tuoteColumn.getTuotteetPanel());
            Program.getInstance().getGui().refresh();
        }));

        JButton customButton = this.getCustomButton();
        if (customButton != null)
        {
            peruutaJaLisaa.add(customButton);
        }

        peruutaJaLisaa.add(lisaa);
        finalPanel.add(peruutaJaLisaa, BorderLayout.PAGE_END);

        this.tuoteColumn.getBorder().add(finalPanel, BorderLayout.CENTER);

        lisaa.addActionListener(l1 -> {

            if (this.test()) {
                virhe.setText(" Virheitä löytyi");
                return;
            }

            virhe.setText("");
            otsikko.setText(this.getStatus());

            String result = this.insert();

            this.tuoteColumn.hae();

            this.tuoteColumn.listaaTuotteet();

            finalPanel.remove(peruutaJaLisaa);
            if (custom != null)
                finalPanel.remove(custom);

            JTextArea virheLog = TextComponentHelper.createFixedTextArea();
            virheLog.setText(result);
            virheLog.setEditable(false);
            virheLog.setLineWrap(true);
            finalPanel.add(virheLog, BorderLayout.CENTER);

            finalPanel.add(GuiHelper.createExtraPanel(GuiHelper.createButton("Valmis", l2 -> {
                this.tuoteColumn.getBorder().remove(finalPanel);
                this.tuoteColumn.getBorder().add(this.tuoteColumn.getTuotteetPanel());
                Program.getInstance().getGui().refresh();
            })), BorderLayout.PAGE_END);
        });

        Program.getInstance().getGui().refresh();
    }

    public JComponent addCustomComponents() {
        return null;
    }

    public boolean test() {
        return false;
    }

    public abstract String getOstikko();

    public abstract String getSendButtonName();

    public abstract String getStatus();

    public abstract String insert();

    public JButton getCustomButton()
    {
        return null;
    }
}
