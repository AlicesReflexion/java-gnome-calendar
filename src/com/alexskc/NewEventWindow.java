package com.alexskc;

import org.gnome.gtk.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;

import static com.alexskc.GetFromDb.createEvent;

/**
 * Created by alexskc on 2/2/13.
 */
public class NewEventWindow extends Window {
    LocalDate currentDate;
    String currentUser;
    public NewEventWindow(LocalDate date, String username) {
        currentDate = date;
        currentUser = username;
        setTitle("New Event on " + date.toString());
        initUI();

        setDefaultSize(250, 230);
        setPosition(WindowPosition.CENTER);
        showAll();
    }

    Event event = new Event();
    boolean validStartTime = false;
    boolean validEndTime = false;
    public void initUI() {
        VBox vBox = new VBox(true, 2);
        Table table = new Table(5, 2, true);

        Entry titleEntry = new Entry();
        titleEntry.connect(new Entry.Changed() {
            @Override
            public void onChanged(Entry entry) {
                event.Title = entry.getText();
            }
        });
        table.attach(new Label("Event Title"), 0, 1, 0, 1);
        table.attach(titleEntry, 1, 2, 0, 1);

        Entry startEntry = new Entry();
        startEntry.connect(new Entry.Changed() {
            @Override
            public void onChanged(Entry entry) {
                try {
                    event.startTime = LocalTime.parse(entry.getText(), DateTimeFormatter.ofPattern("HH:m"));
                    validStartTime = true;
                } catch (DateTimeParseException e) {
                    validStartTime = false;
                }
            }
        });
        table.attach(new Label("Start Time"), 0, 1, 1, 2);
        table.attach(startEntry, 1, 2, 1, 2);

        Entry endEntry = new Entry();
        endEntry.connect(new Entry.Changed() {
            @Override
            public void onChanged(Entry entry) {
                try {
                    event.endTime = LocalTime.parse(entry.getText(), DateTimeFormatter.ofPattern("HH:m"));
                    validEndTime = true;
                } catch (DateTimeParseException e) {
                    validEndTime = false;
                }
            }
        });
        table.attach(new Label("End Time"), 0, 1, 2, 3);
        table.attach(endEntry, 1, 2, 2, 3);

        Entry otherCal = new Entry();
        final String[] otherUser = {""};
        otherCal.connect(new Entry.Changed() {
            @Override
            public void onChanged(Entry entry) {
                otherUser[0] = entry.getText();
            }
        });
        table.attach(new Label("Conflicting Calendar"), 0, 1, 3, 4);
        table.attach(otherCal, 1, 2, 3, 4);

        Button create = new Button("Create Event");
        create.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                if (validStartTime == false || validEndTime == false || event.endTime.isBefore(event.startTime)) {
                    MessageDialog md = new MessageDialog((Window) create.getToplevel(), true, MessageType.ERROR, ButtonsType.CLOSE, "Incorrect time format!");
                    md.setPosition(WindowPosition.CENTER);
                    md.run();
                    md.hide();
                } else {
                    event.year = currentDate.getYear();
                    event.month = currentDate.getMonthValue();
                    event.day = currentDate.getDayOfMonth();
                    event.username = currentUser;
                    Random rand = new Random();
                    event.Id = rand.nextInt();
                    createEvent(event);
                }
            }
        });
        table.attach(create, 1, 2, 4, 5);

        Button resolve = new Button("Find potential times");
        resolve.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                List<Event> possibleTimeEvents = com.alexskc.GetFromDb.possibleTimes(currentDate, currentUser, otherUser[0]);
                String suggestedTimes = "Possible Times:\n";
                for (Event event : possibleTimeEvents) {
                    suggestedTimes = suggestedTimes.concat(event.startTime + "-" + event.endTime + "\n");
                }
                MessageDialog md = new MessageDialog((Window) create.getToplevel(), true, MessageType.INFO, ButtonsType.CLOSE, suggestedTimes);
                md.setPosition(WindowPosition.CENTER);
                md.run();
                md.hide();
            }
        });
        table.attach(resolve, 0, 1, 4, 5);


        vBox.packStart(table, true, true, 0);
        add(vBox);
    }
}
