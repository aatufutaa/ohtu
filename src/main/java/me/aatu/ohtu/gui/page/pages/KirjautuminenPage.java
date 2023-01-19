package me.aatu.ohtu.gui.page.pages;

import lombok.AllArgsConstructor;
import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.page.GuiPage;

import javax.swing.*;

@AllArgsConstructor
public class KirjautuminenPage extends GuiPage {

    private final String msg;

    @Override
    public JPanel getContentPanel() {
        return GuiHelper.createTitledPanel("Kirjautuminen", new JLabel(this.msg));
    }
}
