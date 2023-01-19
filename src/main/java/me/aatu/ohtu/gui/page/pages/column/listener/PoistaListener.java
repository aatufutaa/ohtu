package me.aatu.ohtu.gui.page.pages.column.listener;

import me.aatu.ohtu.gui.textcomponent.TextComponentHelper;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;

import javax.swing.*;

public abstract class PoistaListener<T> extends TuoteButtonListener<T> {

    public PoistaListener(TuoteColumn<T> mokkiColumn) {
        super(mokkiColumn);
    }

    @Override
    public JComponent addCustomComponents() {
        T asiakas = this.tuoteColumn.getValittuTuote();
        JTextArea virheLog = TextComponentHelper.createFixedTextArea();
        virheLog.setText(this.getLog(asiakas));
        virheLog.setEditable(false);
        virheLog.setLineWrap(true);
        return virheLog;
    }

    @Override
    public String getSendButtonName() {
        return "Poista";
    }

    public abstract String getLog(T tuote);
}
