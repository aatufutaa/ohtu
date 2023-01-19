package me.aatu.ohtu.gui.textcomponent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TextComponentHelper {

    // Korjaan oudon bugin, jossa JTextArea border/font on pielessä
    private static final Border DEFAULT_BORDER;
    private static final Font DEFAULT_FONT;

    static {
        JTextField textField = new JTextField();
        DEFAULT_BORDER = textField.getBorder();
        DEFAULT_FONT = textField.getFont();
    }

    public static JTextArea createFixedTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBorder(DEFAULT_BORDER);
        textArea.setFont(DEFAULT_FONT);
        return textArea;
    }
    // ---------

    public static JTextField createTextField(int maxLength) {
        JTextField textField = new JTextField();
        textField.setDocument(new TextComponentLimit(maxLength));

        return textField;
    }

    public static JTextArea createTextArea(int maxLength) {
        JTextArea textArea = createFixedTextArea();
        textArea.setLineWrap(true);
        textArea.setDocument(new TextComponentLimit(maxLength));
        return textArea;
    }


}
