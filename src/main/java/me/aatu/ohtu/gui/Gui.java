package me.aatu.ohtu.gui;

import lombok.Getter;
import me.aatu.ohtu.gui.options.GuiOptions;
import me.aatu.ohtu.gui.page.GuiPage;
import me.aatu.ohtu.gui.page.pages.KirjautuminenPage;

import javax.swing.*;

public class Gui {

    private final JFrame frame;
    @Getter private final GuiOptions guiOptions;

    public Gui() {
        this.frame = new JFrame();
        this.guiOptions = new GuiOptions();
    }

    public void create() {
        this.frame.setSize(800, 600);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setTitle("Mökkien Hallintajärjestelmä");
        this.frame.setVisible(true);

        this.showMsg("Luodaan yhteys tietokantaan...");
    }

    public void showMsg(String msg) {
        this.showPage(new KirjautuminenPage(msg));
    }

    public void showPage(GuiPage page) {
        this.frame.setContentPane(page.getContentPanel());

        this.refresh();
    }

    public void refresh() {
        this.frame.invalidate();
        this.frame.validate();
        this.frame.repaint();
    }
}
