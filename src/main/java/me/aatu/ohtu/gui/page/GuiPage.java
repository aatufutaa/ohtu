package me.aatu.ohtu.gui.page;

import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.Gui;

import javax.swing.*;

public abstract class GuiPage {

    protected final Gui gui = Program.getInstance().getGui();

    public abstract JPanel getContentPanel();
}
