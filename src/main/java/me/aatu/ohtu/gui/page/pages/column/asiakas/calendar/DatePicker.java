package me.aatu.ohtu.gui.page.pages.column.asiakas.calendar;

import lombok.Getter;
import me.aatu.ohtu.gui.helper.GuiHelper;
import me.aatu.ohtu.gui.page.pages.column.asiakas.listener.VaraaListener;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePicker {

    @Getter private Date selectedDate;
    @Getter private final Kalenteri kalenteri;
    @Getter private final JPanel panel;
    private final JLabel error;

    public DatePicker(String name, VaraaListener varaaListener) {
        JLabel alkuPvmText = new JLabel(name + ":");
        JTextField alkuPvmField = GuiHelper.createNotEditableTextField();

        JPanel calendarPanel = GuiHelper.createVerticalPanel();
        Calendar cal2 = new GregorianCalendar();
        cal2.set(Calendar.DAY_OF_MONTH, 1);

        this.kalenteri = new Kalenteri(cal2, new CalendarSelectLister() {
            @Override
            public void onSelect(int y, int m, int d) {
                Date date = new GregorianCalendar(y, m, d).getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                alkuPvmField.setText(simpleDateFormat.format(date));
                DatePicker.this.selectedDate = date;
            }

            @Override
            public void onNextPage() {
                varaaListener.updateKalenteriColors(DatePicker.this.kalenteri);
            }
        });

        calendarPanel.add(this.kalenteri);
        calendarPanel.setVisible(false);
        JButton datePicker = new JButton("...");
        datePicker.setMargin(new Insets(1, 6, 1, 6));
        datePicker.addActionListener(l -> {
            calendarPanel.setVisible(!calendarPanel.isVisible());
        });
        JPanel datePanel = GuiHelper.createVerticalPanel();

        JPanel textPanel = GuiHelper.createBorderPanel();
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        textPanel.add(alkuPvmText, BorderLayout.LINE_START);
        this.error = GuiHelper.createErrorLabel();
        textPanel.add(this.error, BorderLayout.CENTER);
        datePanel.add(textPanel);

        JPanel datePanel2 = GuiHelper.createHorizontalPanel();
        datePanel2.add(alkuPvmField);
        datePanel2.add(datePicker);
        datePanel.add(datePanel2);

        JPanel finalCalendarPanel = GuiHelper.createVerticalPanel();
        finalCalendarPanel.add(datePanel);
        finalCalendarPanel.add(calendarPanel);
        finalCalendarPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        this.panel = finalCalendarPanel;
    }

    public void setError(String err) {
        this.error.setText(err.length() == 0 ? err : " " + err);
    }

    public boolean test() {
        if (this.getSelectedDate() == null) {
            this.setError("Pvm ei ole asetettu");
            return false;
        }
        this.setError("");
        return true;
    }
}
