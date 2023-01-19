package me.aatu.ohtu.gui.page.pages.column.asiakas.calendar;

import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.Program;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Kalenteri extends JPanel {
    private static final Color DEFAULT_BG = new JButton().getBackground();

    private static final String[] KUUKAUDET = new String[] {
            "tammikuu", "helmikuu", "maaliskuu", "huhtikuu", "toukokuu", "kesäkuu", "heinäkuu", "elokuu", "syyskuu", "lokakuu", "marraskuu", "joulukuu"
    };

    private static final String[] VIIKONPAIVAT = new String[] {
            "ma", "ti", "ke", "to", "pe", "la", "su"
    };

    private Calendar calendar;
    private final CalendarSelectLister lister;

    private JPanel panel;

    private final JLabel calendarTitle = new JLabel();
    private final JButton[] buttons = new JButton[42];

    private int[] monthIndex;

    public int getYear() {
        return getYear(this.calendar);
    }

    public int getMonth() {
        return getMonth(this.calendar);
    }

    public Kalenteri(Calendar calendar, CalendarSelectLister lister) {
        this.calendar = calendar;
        this.lister = lister;

        this.setLayout(new BorderLayout());

        this.panel = this.createView();

        JButton previous = GuiHelper.createButton("<", l -> {
            this.calendar = getPreviousMonth(this.calendar);
            this.updateView();
        });

        JButton next = GuiHelper.createButton(">", l -> {
            this.calendar = getNextMonth(this.calendar);
            this.updateView();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(previous);
        buttonPanel.add(this.calendarTitle);
        buttonPanel.add(next);

        this.add(buttonPanel, BorderLayout.PAGE_START);
        this.add(this.panel, BorderLayout.CENTER);
    }

    private void updateView() {
        this.remove(this.panel);
        this.add(this.panel = this.createView(), BorderLayout.CENTER);
        this.lister.onNextPage();
        Program.getInstance().getGui().refresh();
    }

    public void reset() {
        for (JButton btn : this.buttons) {
            btn.setBackground(DEFAULT_BG);
        }
    }

    public void setColor(int day, Color color) {
        JButton btn = this.buttons[this.monthIndex[0] + day - 1];
        btn.setBackground(color);
    }

    private JPanel createView() {
        int year = this.getYear();
        int month = this.getMonth();

        this.calendarTitle.setText(KUUKAUDET[month] + " " + year);

        JPanel panel = new JPanel(new GridLayout(0, 7, 0, 0));

        for (String v : VIIKONPAIVAT) {
            JLabel l = new JLabel(v);
            l.setHorizontalAlignment(JLabel.CENTER);
            panel.add(l);
        }

        int daysInMonth = getDaysInMonth(year, month);
        int daysInPreviousMonth = getDaysInMonth(year, getPreviousMonth(month));
        int starDay = getFixedStartDay(this.calendar.get(Calendar.DAY_OF_WEEK)) - 1;

        this.monthIndex = new int[] {starDay, starDay + daysInMonth - 1};
        for (int i = 0; i < this.buttons.length; i++) {
            JButton button = new JButton();
            if (i >= this.monthIndex[0] && i <= this.monthIndex[1])
                button.addActionListener(l -> this.lister.onSelect(this.getYear(), this.getMonth(), Integer.parseInt(button.getText())));
            button.setMargin(new Insets(4, 4, 4, 4));
            if (i < this.monthIndex[0]) {
                button.setText(daysInPreviousMonth - starDay + 1 + i + "");
                button.setForeground(Color.lightGray);
            } else if (i <= this.monthIndex[1]) {
                button.setText(i - starDay + 1 + "");
                button.setForeground(Color.BLACK);
            } else {
                button.setText(i - starDay - daysInMonth + 1 + "");
                button.setForeground(Color.LIGHT_GRAY);
            }
            this.buttons[i] = button;
        }

        for (JButton b : this.buttons) {
            panel.add(b);
        }

        return panel;
    }

    private static Calendar getNextMonth(Calendar calendar) {
        return new GregorianCalendar(
                getYear(calendar),
                getMonth(calendar) + 1,
                1);
    }

    private static Calendar getPreviousMonth(Calendar calendar) {
        return new GregorianCalendar(
                getYear(calendar),
                getMonth(calendar) - 1,
                1);
    }

    private static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    private static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    private static int getFixedStartDay(int day) {
        return day == 1 ? 7 : day - 1;
    }

    private static int getPreviousMonth(int month) {
        return month == 0 ? 11 : month - 1;
    }

    private static int getDaysInMonth(int year, int month) {
        YearMonth yearMonthObject = YearMonth.of(year + (month == 11 ? -1 : 0), month + 1);
        return yearMonthObject.lengthOfMonth();
    }
}
