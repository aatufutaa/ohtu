package me.aatu.ohtu.gui.helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GuiHelper {

    public static JButton createButton(String name, ActionListener listener) {
        JButton button = new JButton(name);
        button.addActionListener(listener);
        return button;
    }

    public static JComboBox<String> createDropDown(String[] names) {
        return new JComboBox<>(names);
    }

    public static JPanel createTitledPanel(String title, JComponent... component) {
        JPanel center = new JPanel();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < component.length; i++) {
            panel.add(component[i]);
            if (i != component.length - 1)
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createTitledBorder(title));
        borderPanel.add(panel);

        center.setLayout(new GridBagLayout());
        center.add(borderPanel, new GridBagConstraints());

        return center;
    }

    public static JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    public static JLabel createErrorLabel() {
        JLabel error = new JLabel();
        error.setForeground(Color.RED);
        return error;
    }

    public static JPanel createHorizontalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

    public static JPanel createBorderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        return panel;
    }

    public static JPanel createTitledBorder(String title, JPanel child) {
        JPanel padding = new JPanel();
        JPanel border = new JPanel();

        padding.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        border.setBorder(BorderFactory.createTitledBorder(title));

        padding.add(child);
        border.add(padding);
        return border;
    }

    public static JPanel createExtraPanel(JComponent child) {
        JPanel panel = new JPanel();
        panel.add(child);
        return panel;
    }

    public static JTextField createNotEditableTextField() {
        JTextField valittuAsiakas = new JTextField();
        valittuAsiakas.setEditable(false);
        return valittuAsiakas;
    }

    public static JTextField createFieldWithText(String msg, JPanel root, JButton btn) {
        JPanel asiakasPanel = GuiHelper.createVerticalPanel();
        asiakasPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel text = new JLabel(msg);
        JPanel valittuPanel = new JPanel();
        valittuPanel.setLayout(new BorderLayout());
        valittuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        valittuPanel.add(text, BorderLayout.LINE_START);
        asiakasPanel.add(valittuPanel);
        JPanel valitseAsiakasPanel = GuiHelper.createHorizontalPanel();
        JTextField valittuAsiakas = createNotEditableTextField();
        valitseAsiakasPanel.add(valittuAsiakas);
        if (btn != null)
            valitseAsiakasPanel.add(btn);
        asiakasPanel.add(valitseAsiakasPanel);
        root.add(asiakasPanel);
        return valittuAsiakas;
    }

    public static void setWindowsTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
