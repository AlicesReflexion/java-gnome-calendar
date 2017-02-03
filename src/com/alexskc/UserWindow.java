package com.alexskc;

import org.gnome.gdk.Event;
import org.gnome.gtk.*;

import com.alexskc.GetFromDb;

/**
 * Created by alexskc on 2/2/13.
 */
public class UserWindow extends Window {

    public UserWindow() {
        setTitle("Calendar");

        GetFromDb.initiate();
        initUi(this);

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

    public void initUi(Window window) {
        VBox vBox = new VBox(true, 2);
        Table table = new Table(2, 1, true);
        Entry usernameEntry = new Entry();
        final String[] username = {""};
        usernameEntry.connect(new Entry.Changed() {
            @Override
            public void onChanged(Entry entry) {
                username[0] = entry.getText();
            }
        });
        table.attach(usernameEntry, 0, 1, 0, 1);
        Button loginButton = new Button("Login");
        loginButton.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button button) {
                if (!username[0].equals("")) {
                    window.destroy();
                    new CalendarWindow(username[0]);
                } else {
                    MessageDialog md = new MessageDialog((Window) loginButton.getToplevel(), true, MessageType.ERROR, ButtonsType.CLOSE, "Must enter username");
                    md.setPosition(WindowPosition.CENTER);
                    md.run();
                    md.hide();
                }
            }
        });
        table.attach(loginButton, 0, 1, 1, 2);
        vBox.packStart(table, true, true, 0);
        add(vBox);
    }

    public static void main(String[] args) {
        Gtk.init(args);
        new UserWindow();
        Gtk.main();
    }
}
