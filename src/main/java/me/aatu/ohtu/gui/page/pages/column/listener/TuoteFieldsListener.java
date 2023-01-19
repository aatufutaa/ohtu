package me.aatu.ohtu.gui.page.pages.column.listener;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;
import me.aatu.ohtu.gui.page.pages.column.TuoteColumn;

import javax.swing.*;
import java.awt.*;

public abstract class TuoteFieldsListener<T> extends TuoteButtonListener<T> {

    public TuoteFieldsListener(TuoteColumn<T> tuoteColumn) {
        super(tuoteColumn);
    }

    @Override
    public JComponent addCustomComponents() {
        JPanel panel = GuiHelper.createVerticalPanel();

        for (int i = 0; i < this.tuoteColumn.getTextComponentOptions().size(); i++) {
            TextComponentField field = this.tuoteColumn.getTextComponentOptions().get(i);
            this.initField(i, field);
            panel.add(field.getPanel());
        }

        JPanel textFieldPanel = GuiHelper.createBorderPanel();
        textFieldPanel.add(panel, BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textFieldPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    protected void initField(int index, TextComponentField field) {
        field.reset();
    }

    @Override
    public boolean test() {
        boolean errorsFound = false;
        for (TextComponentField option : this.tuoteColumn.getTextComponentOptions()) {
            if (!option.test()) {
                errorsFound = true;
            }
        }
        return errorsFound;
    }
}
