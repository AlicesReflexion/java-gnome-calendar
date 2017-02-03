package com.alexskc;

import java.time.LocalTime;

/**
 * Created by alexskc on 2/2/13.
 */
public class Event {
    int Id;
    String Title;
    String username;
    LocalTime startTime;
    LocalTime endTime;
    int year;
    int month;
    int day;

    public Event clone() {
        Event event = new Event();
        event.Id = this.Id;
        event.Title = this.Title;
        event.username = this.username;
        event.startTime = this.startTime;
        event.endTime = this.endTime;
        event.year = this.year;
        event.month = this.month;
        event.day = this.day;
        return event;
    }
}
