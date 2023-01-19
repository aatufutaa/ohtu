package me.aatu.ohtu.gui.page.pages.column;

import lombok.Getter;
import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.page.pages.HallintaSivu;
import me.aatu.ohtu.gui.page.pages.column.asiakas.AsiakasColumn;
import me.aatu.ohtu.gui.textcomponent.TextComponentField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class TuoteColumn<T> extends PageColumn {

    @Getter private final List<TextComponentField> textComponentOptions = this.getTextComponents();

    public HallintaSivu hallintaSivu;

    public TuoteColumn(HallintaSivu hallintaSivu) {
        this.hallintaSivu = hallintaSivu;
    }

    protected abstract List<TextComponentField> getTextComponents();

    protected abstract String[] getHakuTyypit();

    protected abstract String getTitle();

    protected Map<Integer, T> tuoteMap;

    private final JLabel tuotteet = new JLabel();

    private final JComboBox<String> hakuTyyppi = new JComboBox<>(this.getHakuTyypit());

    private final JScrollPane scrollableList = new JScrollPane();

    protected final JButton lisaa = createToimintaButton("Lisää", false);
    protected final JButton muokkaa = createToimintaButton("Muokkaa", true);
    protected final JButton poista = createToimintaButton("Poista", true);

    protected final JButton teeVaraus = createToimintaButton("Varaa", true);
    protected final JButton varaukset = createToimintaButton("Varaukset", true);
    protected final JButton laskut = createToimintaButton("Laskut", true);

    private final JTextField haeField = new JTextField();

    @Getter private final JPanel border = new JPanel();

    @Getter private final JPanel tuotteetPanel = new JPanel();

    private int valittuId = -1;

    private void paivitaTuotteidenMaara(int i) {
        this.tuotteet.setText(this.getTitle() + " (" + i + "kpl)");
    }

    protected abstract Map<Integer, T> haeData();

    @Override
    public void hae() {
        boolean paivita = this.tuoteMap == null;
        this.tuoteMap = this.haeData();
        if (paivita) {
            this.paivitaTuotteidenMaara(this.tuoteMap.size());
        }
    }

    protected abstract List<String> suodata(String hakuKriteeri, int suodatin);

    public void listaaTuotteet() {
        int suodatin = this.hakuTyyppi.getSelectedIndex(); // id vai nimi
        String hakuKriteeri = this.haeField.getText().toLowerCase();

        java.util.List<String> nimet = this.suodata(hakuKriteeri, suodatin);

        JList<String> list = new JList<>(nimet.toArray(String[]::new));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(l -> {
            this.muokkaa.setEnabled(true);
            this.poista.setEnabled(true);
            this.valittuId = Integer.parseInt(list.getSelectedValue().split(" ")[0]);
            this.hallintaSivu.onTuoteMuutettu(this);
            this.onSelect(this.valittuId);
        });
        this.scrollableList.setViewportView(list);

        this.muokkaa.setEnabled(false);
        this.poista.setEnabled(false);
        this.valittuId = -1;
        this.hallintaSivu.onTuoteMuutettu(this);
        this.onSelect(this.valittuId);

        this.paivitaTuotteidenMaara(nimet.size());
    }

    public T getValittuTuote() {
        if (this.valittuId == -1)
            return null;
        return this.tuoteMap.get(this.valittuId);
    }

    protected void onSelect(int id) {
        if (id == -1) {
            this.teeVaraus.setEnabled(false);
            this.varaukset.setEnabled(false);
            this.laskut.setEnabled(false);
        } else {
            this.teeVaraus.setEnabled(true);
            this.varaukset.setEnabled(true);
            this.laskut.setEnabled(true);
        }
    }

    protected abstract void addListeners();

    private void initComponents() {
        this.border.setLayout(new GridLayout(0, 1));
        int borderSize = 10;
        this.border.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
        this.border.add(this.tuotteetPanel);

        this.tuotteetPanel.setLayout(new BorderLayout());
        this.tuotteetPanel.add(this.scrollableList, BorderLayout.CENTER);

        this.addListeners();

        this.hakuTyyppi.addActionListener(e -> this.listaaTuotteet());

        this.haeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                TuoteColumn.this.listaaTuotteet();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                TuoteColumn.this.listaaTuotteet();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                TuoteColumn.this.listaaTuotteet();
            }
        });

        JPanel hae = new JPanel();
        hae.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        hae.setLayout(new BoxLayout(hae, BoxLayout.X_AXIS));
        JLabel haeText = new JLabel("Hae:");
        haeText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        hae.add(haeText);
        hae.add(this.haeField);
        hae.add(this.hakuTyyppi);

        JPanel napit = new JPanel();
        napit.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        napit.setLayout(new GridLayout(0, 3));
        napit.add(this.lisaa);
        napit.add(this.muokkaa);
        napit.add(this.poista);

        JPanel varaus = new JPanel();
        varaus.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        varaus.setLayout(new GridLayout(0, 3));
        varaus.add(this.teeVaraus);
        varaus.add(this.varaukset);
        varaus.add(this.laskut);

        JPanel header = GuiHelper.createVerticalPanel();
        header.add(GuiHelper.createExtraPanel(this.tuotteet));
        header.add(hae);
        header.add(napit);
        if (this instanceof AsiakasColumn)
            header.add(varaus);

        this.tuotteetPanel.add(header, BorderLayout.PAGE_START);
    }

    @Override
    public JPanel getPanel() {
        this.initComponents();

        this.listaaTuotteet();

        return this.border;
    }

    private static JButton createToimintaButton(String name, boolean disable) {
        JButton button = new JButton(name);
        button.setMargin(new Insets(2, 0, 2, 0));
        if (disable)
            button.setEnabled(false);
        return button;
    }
}
