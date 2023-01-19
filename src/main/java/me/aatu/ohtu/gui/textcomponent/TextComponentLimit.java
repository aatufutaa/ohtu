package me.aatu.ohtu.gui.textcomponent;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextComponentLimit extends PlainDocument {

    private final int maxLength;

    public TextComponentLimit(int maxLength) {
        super();
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        //Poistaa uudet rivit tekstikentästä
        if (str.equals("\n"))
            return;

        if (this.getLength() + str.length() <= this.maxLength) {
            super.insertString(offset, str, attr);
        }
    }

}
