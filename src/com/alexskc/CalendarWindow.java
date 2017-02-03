package com.alexskc;

import org.gnome.gdk.Event;
import org.gnome.gdk.RGBA;
import org.gnome.gtk.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @author alexskc
 */

public class CalendarWindow extends Window {

    public CalendarWindow(String username) {
        setTitle(username + "'s Calendar");
        currentUser = username;
    
        initUI();
    
        connect(new Window.DeleteEvent() {
            public boolean onDeleteEvent(Widget source, Event event) {
                Gtk.mainQuit();
                return false;
            }
        });
    
        setDefaultSize(250, 230);
        setPosition(WindowPosition.CENTER);
        showAll();
    }

    String currentUser;
    public static YearMonth currentMonth = YearMonth.now();


    public Table dayButtons(YearMonth currentMonth) {
        List<LocalDate> dates = com.alexskc.GetFromDb.getDates(currentMonth, currentUser);
        int currentWeek = 0;
        Table table = new Table(5, 7, true);
        for (int i = 1; i <= currentMonth.lengthOfMonth(); i++) {
            LocalDate currentDay = currentMonth.atDay(i);
            DayOfWeek weekDay = currentDay.getDayOfWeek();
            if (currentMonth.atDay(i).getDayOfWeek().getValue() == 1 && i > 1) {
                currentWeek += 1;
            }
            Button currentDayButton = new Button(Integer.toString(i));
            if (currentDay.equals(LocalDate.now())) {
                currentDayButton.overrideColor(StateFlags.NORMAL, RGBA.BLUE);
            }
            if (dates.contains(currentDay)) {
                currentDayButton.overrideColor(StateFlags.NORMAL, RGBA.RED);
            }
            currentDayButton.connect(new Button.Clicked() {
                @Override
                public void onClicked(Button button) {
                    new EventsWindow(currentDay, currentUser);
                }
            });
            table.attach(currentDayButton, weekDay.getValue() - 1, weekDay.getValue(), currentWeek, currentWeek + 1);
        }
        return table;
    }
    
    
    public void initUI() {

        VBox vbox = new VBox(false, 2);

        Table table = new Table(7, 7, true);
        final Table[] monthTable = {new Table(5, 7, true)};

        Label currentMonthLabel = new Label(currentMonth.format(DateTimeFormatter.ofPattern("MMMM y")));
        table.attach(currentMonthLabel, 1, 6, 0, 1);
        monthTable[0] = dayButtons(currentMonth);
        Button nextMonth = new Button(">");
        Button prevMonth = new Button("<");
        nextMonth.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                currentMonth = currentMonth.plusMonths(1);
                currentMonthLabel.setLabel(currentMonth.format(DateTimeFormatter.ofPattern("MMMM y")));
                monthTable[0].destroy();
                monthTable[0] = dayButtons(currentMonth);
                table.attach(monthTable[0], 0, 7, 2, 7);
                table.showAll();
            }
        });
        prevMonth.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                currentMonth = currentMonth.minusMonths(1);
                currentMonthLabel.setLabel(currentMonth.format(DateTimeFormatter.ofPattern("MMMM y")));
                monthTable[0].destroy();
                monthTable[0] = dayButtons(currentMonth);
                table.attach(monthTable[0], 0, 7, 2, 7);
                table.showAll();
            }
        });

        table.attach(prevMonth, 0,1, 0, 1);
        table.attach(nextMonth, 6, 7, 0, 1);

        table.attach(new Label("Mon"), 0, 1, 1, 2);
        table.attach(new Label("Tue"), 1, 2, 1, 2);
        table.attach(new Label("Wed"), 2, 3, 1, 2);
        table.attach(new Label("Thu"), 3, 4, 1, 2);
        table.attach(new Label("Fri"), 4, 5, 1, 2);
        table.attach(new Label("Sat"), 5, 6, 1, 2);
        table.attach(new Label("Sun"), 6, 7, 1, 2);

        table.attach(monthTable[0], 0, 7, 2, 7);
        table.showAll();

        vbox.packStart(table, true, true, 0);

        add(vbox);
    }
}
