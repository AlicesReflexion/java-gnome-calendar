package com.alexskc;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;

import org.gnome.gdk.*;
import org.gnome.gtk.*;
import org.gnome.gtk.Button;
import org.gnome.gtk.Window;

import static com.alexskc.GetFromDb.getEvents;

/**
 * Created by alexskc on 2/2/13.
 */
public class EventsWindow extends Window {
    public LocalDate windowDate;
    String currentUser;

    public EventsWindow(LocalDate date, String Username) {
        windowDate = date;
        currentUser = Username;
        setTitle("Events " + windowDate.toString());
        initUI();

        setDefaultSize(250, 230);
        setPosition(WindowPosition.CENTER);
        showAll();
    }

    DataColumnInteger idColumn = new DataColumnInteger();
    public void initUI() {
        VBox vbox = new VBox(false, 2);

        Table table = new Table(2,2,true);
        TreeView view = initEvents();
        table.attach(view, 0, 1, 0, 2);

        Button newEventButton = new Button("New Event");
        newEventButton.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                new NewEventWindow(windowDate, currentUser);
            }
        });
        table.attach(newEventButton, 1, 2, 0, 1);

        Button deleteEventButton = new Button("Delete Event");
        deleteEventButton.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                ListStore model = (ListStore) view.getModel();
                TreeIter selection = view.getSelection().getSelected();
                com.alexskc.GetFromDb.deleteEvent(model.getValue(selection, idColumn));
                if (selection != null) model.removeRow(selection);
            }
        });
        table.attach(deleteEventButton, 1, 2, 1, 2);

        vbox.packStart(table, true, true, 0);
        add(vbox);
    }

    public TreeView initEvents() {
        List<Event> events = getEvents(windowDate, currentUser);
        final ListStore model;
        idColumn = new DataColumnInteger();
        final DataColumnString eventNameColumn = new DataColumnString();
        final DataColumnString startTimeColumn = new DataColumnString();
        final DataColumnString endTimeColumn = new DataColumnString();
        TreeViewColumn vertical;
        CellRendererText text;

        model = new ListStore(new DataColumn[] {
                idColumn,
                eventNameColumn,
                startTimeColumn,
                endTimeColumn
        });
        TreeIter eventIter;
        for (Event event: events) {
            eventIter = model.appendRow();
            model.setValue(eventIter, idColumn, event.Id);
            model.setValue(eventIter, eventNameColumn, event.Title);
            model.setValue(eventIter, startTimeColumn, event.startTime.format(DateTimeFormatter.ofPattern("HH:m")));
            model.setValue(eventIter, endTimeColumn, event.endTime.format(DateTimeFormatter.ofPattern("HH:m")));
        }
        model.setSortColumn(startTimeColumn, SortType.ASCENDING);

        TreeView view = new TreeView(model);

        vertical = view.appendColumn();
        vertical.setTitle("Event");
        text = new CellRendererText(vertical);
        text.setText(eventNameColumn);

        vertical = view.appendColumn();
        vertical.setTitle("Start Time");
        text = new CellRendererText(vertical);
        text.setText(startTimeColumn);

        vertical = view.appendColumn();
        vertical.setTitle("End Time");
        text = new CellRendererText(vertical);
        text.setText(endTimeColumn);

        return view;
    }
}
