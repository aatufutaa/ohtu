package me.aatu.ohtu;

import lombok.Getter;
import me.aatu.ohtu.database.DatabaseManager;
import me.aatu.ohtu.gui.Gui;
import me.aatu.ohtu.gui.helper.GuiHelper;

public class Program {

    @Getter private static Program instance;

    @Getter private final Gui gui;
    @Getter private final DatabaseManager databaseManager;

    private Program() {
        instance = this;

        GuiHelper.setWindowsTheme();

        this.gui = new Gui();
        this.databaseManager = new DatabaseManager();
    }

    private void start() {
        this.gui.create();
        this.databaseManager.start();
    }

    private void exit() {
        this.databaseManager.stop();
    }

    public static void main(String[] args) {
        new Program().start();
    }
}