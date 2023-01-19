package me.aatu.ohtu.gui.page.pages;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.Program;
import me.aatu.ohtu.gui.page.GuiPage;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class AlueenValintaPage extends GuiPage {

    @Override
    public JPanel getContentPanel() {
        Map<String, Integer> alueMap = new HashMap<>();

        Program.getInstance().getDatabaseManager().query("select * from alue", (rs) -> {
            while (rs.next()) {
                int id = rs.getInt("alue_id");
                String nimi = rs.getString("nimi");
                alueMap.put(nimi, id);
            }
        });

        JLabel valitseAlue = new JLabel("Valitse alue");

        JComboBox<String> alueet = GuiHelper.createDropDown(alueMap.keySet().toArray(String[]::new));

        if (this.gui.getGuiOptions().getAlue() != null)
            alueet.setSelectedItem(this.gui.getGuiOptions().getAlue());

        JButton seuraava = GuiHelper.createButton("Seuraava", (e) -> {
            String selected = (String) alueet.getSelectedItem();
            this.gui.getGuiOptions().setAlue(selected);
            this.gui.getGuiOptions().setCurrentId(alueMap.get(selected));
            this.gui.showPage(new HallintaSivu());
        });

        return GuiHelper.createTitledPanel("Alueen valinta", valitseAlue, alueet, seuraava);
    }
}
