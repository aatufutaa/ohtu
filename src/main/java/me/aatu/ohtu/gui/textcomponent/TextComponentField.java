package me.aatu.ohtu.gui.textcomponent;

import lombok.Getter;
import me.aatu.ohtu.gui.helper.GuiHelper;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

@Getter
public class TextComponentField {

    private final TextComponentType type;
    private final int order;

    private final JPanel panel;
    private final JLabel error;
    private final JTextComponent component;

    public TextComponentField(String name, int maxLength, boolean area, TextComponentType type, int order) {
        this.type = type;
        this.order = order;

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(new JLabel(name + ":"));

        JLabel error = new JLabel();
        error.setForeground(Color.RED);
        namePanel.add(error);
        this.error = error;

        JPanel nameAndErrorPanel = new JPanel();
        nameAndErrorPanel.setLayout(new BorderLayout());
        nameAndErrorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        nameAndErrorPanel.add(GuiHelper.createExtraPanel(namePanel), BorderLayout.LINE_START);

        JPanel componentAndNamePanel = new JPanel();
        componentAndNamePanel.setLayout(new BoxLayout(componentAndNamePanel, BoxLayout.Y_AXIS));
        componentAndNamePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        componentAndNamePanel.add(nameAndErrorPanel);
        componentAndNamePanel.add(this.component = area ? TextComponentHelper.createTextArea(maxLength) : TextComponentHelper.createTextField(maxLength));
        this.panel = componentAndNamePanel;
    }

    public void reset() {
        this.error.setText("");
        this.component.setText("");
    }

    public String getStr() {
        return this.component.getText();
    }

    public int getInt() {
        return Integer.parseInt(this.getStr());
    }

    public double getDouble() {
        return Double.parseDouble(this.getStr());
    }

    public void setText(String t) {
        this.component.setText(t);
    }

    public boolean test() {
        String value = this.component.getText();
        String error = null;

        if (value.length() == 0) {
            error = "Ei voi olla tyhjä";
        } else if (this.type == TextComponentType.INTEGER) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                error = "Virheellinen luku";
            }
        } else if (this.type == TextComponentType.DOUBLE) {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                error = "Virheellinen luku";
            }
        }

        if (error != null) {
            this.error.setText(" " + error);
        } else {
            this.error.setText("");
        }

        return error == null;
    }
}