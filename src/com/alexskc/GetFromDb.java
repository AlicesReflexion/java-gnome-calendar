package com.alexskc;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexskc on 2/2/13.
 */
public class GetFromDb {
    public static Connection connection;
    public static void initiate() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/calendar", "calendaruser", "dbpass");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS EVENTS (ID INT PRIMARY KEY NOT NULL," +
                    "TITLE TEXT NOT NULL," +
                    "STARTTIME TIMESTAMP NOT NULL, " +
                    "ENDTIME TIMESTAMP NOT NULL, " +
                    "YEAR INT NOT NULL, " +
                    "MONTH INT NOT NULL, " +
                    "DAY INT NOT NULL, " +
                    "USERNAME TEXT NOT NULL)");
            statement.close();
        } catch (Exception e) {
            System.out.println("Error connecting to DB! " + e.toString());
            System.exit(1);
        }
        System.out.println("Connected Successfully!");
    }

    public static List<LocalDate> getDates(YearMonth yearMonth, String username) {
        List<LocalDate> dates = new ArrayList<>();
        try {

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM EVENTS WHERE YEAR = " + yearMonth.getYear() + " AND MONTH = " + yearMonth.getMonth().getValue() + " AND USERNAME = '"
                    + username + "'";
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                LocalDate date = LocalDate.of(rs.getInt("YEAR"), rs.getInt("MONTH"), rs.getInt("DAY"));
                dates.add(date);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Error getting dates! " + e.toString());
        }
        return dates;
    }

    public static void createEvent(Event event) {
        String query = "";
        try {
            Statement statement = connection.createStatement();
            query = "INSERT INTO EVENTS (ID, TITLE, STARTTIME, ENDTIME, YEAR, MONTH, DAY, USERNAME) VALUES " +
                    "(" + event.Id + ", '" + event.Title + "', '" + Timestamp.valueOf(LocalDateTime.of(LocalDate.of(event.year, event.month, event.day), event.startTime)) + "', '"
                    + Timestamp.valueOf(LocalDateTime.of(LocalDate.of(event.year, event.month, event.day), event.endTime)) + "', " + event.year
                    + ", " + event.month + ", " + event.day + ", '" + event.username + "')";
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("An error occurred creating an event! " + e.toString());
            System.out.println(query);
        }
    }

    public static void deleteEvent(int eventId) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM EVENTS WHERE ID = " + Integer.toString(eventId));
        } catch (Exception e) {
            System.out.println("There was an error deleting an event!" + e.toString());
        }
    }

    public static List<Event> getEvents(LocalDate date, String userName) {
        List<Event> events = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM EVENTS WHERE YEAR = " + Integer.toString(date.getYear()) + " AND MONTH = " + Integer.toString(date.getMonthValue())
                    + " AND DAY = " + Integer.toString(date.getDayOfMonth()) + " AND USERNAME = '" + userName + "'");
            while (rs.next()) {
                Event event = new Event();
                event.year = rs.getInt("YEAR");
                event.month = rs.getInt("MONTH");
                event.day = rs.getInt("DAY");
                event.username = rs.getString("USERNAME");
                event.Title = rs.getString("TITLE");
                event.startTime = rs.getTime("STARTTIME").toLocalTime();
                event.endTime = rs.getTime("ENDTIME").toLocalTime();
                event.Id = rs.getInt("ID");
                events.add(event);
            }
        } catch (Exception e) {
            System.out.println("There was an error fetching events!" + e.toString());
        }
        return events;
    }

    public static List<Event> possibleTimes(LocalDate date, String userNameA, String userNameB) {
        List<Event> events = getEvents(date, userNameA);
        events.addAll(getEvents(date, userNameB));

        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                return (event.startTime.isAfter(t1.startTime)) ? 1 : event.startTime.isBefore(t1.startTime) ? -1 : 0;
            }
        });
        List<Event> inverseEvents = new ArrayList<>();
        Event newEvent = new Event();
        newEvent.startTime = LocalTime.MIN;
        Integer i = 0;
        newEvent.endTime = events.get(i).startTime;
        while (!newEvent.endTime.equals(LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES))) {
            if (newEvent.endTime.isAfter(newEvent.startTime)) inverseEvents.add(newEvent.clone());
            newEvent.startTime = events.get(i).endTime;
            if (i.equals(events.size() - 1)) {
                newEvent.endTime = LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES);
                if (newEvent.endTime.isAfter(newEvent.startTime)) inverseEvents.add(newEvent.clone());
            } else {
                newEvent.endTime = events.get(i+1).startTime;
            }
            i += 1;
        }
        return inverseEvents;
    }
}
