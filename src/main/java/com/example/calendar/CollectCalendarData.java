package com.example.calendar;

import com.example.calendar.dto.InitialData;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.extern.log4j.Log4j2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
public class CollectCalendarData {

    private final static ArrayList<InitialData> plans = new ArrayList<>();

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = CollectCalendarData.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    /**
     * Collects data from users calendar.
     *
     * @param dateOfSubmission The date by which the data will be collected
     * @return  List of Event objects
     * @throws IOException If an error occurs while communicating with the calendar service.
     * @throws GeneralSecurityException If there is a security-related issue with the calendar service.
     */
    public List<Event> getDataFromCalendar(LocalDate dateOfSubmission) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Events events = service.events().list("primary") // primary, account or calendar name as en.lithuanian#holiday@group.v.calendar.google.com
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setTimeMax(new DateTime(java.sql.Date.valueOf(dateOfSubmission)))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        printEvents(events.getItems());

        return events.getItems();
    }

    /**
     * Calculates event duration and converts it to hours.
     *
     * @param eventStart Date and time when the event starts.
     * @param eventEnd Date and time when the event ends.
     * @return Duration in hours - double value.
     */
    public static double calculateDuration(DateTime eventStart, DateTime eventEnd){
        double durationInHours;
        double durationInMinutes;
        Duration duration = Duration.between(
                Instant.ofEpochMilli(eventStart.getValue()),
                Instant.ofEpochMilli(eventEnd.getValue())
        );
        durationInMinutes = duration.toMinutes();
        durationInHours = durationInMinutes/60;
        return durationInHours;
    }

    /**
     * DateTime value converts to LocalDate value.
     *
     * @param dateTime DateTime value.
     * @return LocalDate value.
     */
    public static LocalDate getJustDateFromDateTIme(DateTime dateTime){
        String dataString = dateTime.toString();
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dataString);
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        return localDateTime.toLocalDate();
    }

    /**
     * Prints list of events.
     *
     * @param events List of Event objects
     */
    public void printEvents(List<Event> events){
        if (events.isEmpty()) {
            System.out.println("\nNo upcoming events found.");
        } else {
            System.out.println("\nUpcoming events:");
            for (Event event : events) {
                DateTime eventStart = event.getStart().getDateTime();
                DateTime eventEnd = event.getEnd().getDateTime();
                if (eventStart == null) {
                    eventStart = event.getStart().getDate();
                }
                System.out.printf("%s (%s - %s) %5s\n", event.getSummary(), eventStart, eventEnd, calculateDuration(eventStart, eventEnd));
            }
        }
    }

    /**
     * Generates a list of InitialData objects - adds start and end DateTime.
     *
     * @param events List of Event objects.
     * @return The list of InitialData objects.
     */
    public ArrayList<InitialData> makePlans(List<Event> events){
        if (events.isEmpty()) {
            log.info("\nNo upcoming events found.");
        } else {
            log.info("\nThere are " + events.size() + " events");
            for (Event event : events) {
                DateTime eventStart = event.getStart().getDateTime();
                DateTime eventEnd = event.getEnd().getDateTime();
                if (eventStart == null) {
                    eventStart = event.getStart().getDate();
                }
                plans.add(new InitialData(event.getSummary(), getJustDateFromDateTIme(eventStart), calculateDuration(eventStart, eventEnd)));
            }
        }
        return plans;
    }
}

//        A list of all calendars
//        CalendarList calendarList = service.calendarList().list().execute();
//        for (CalendarListEntry entry : calendarList.getItems()) {
//            System.out.println(entry);
//        }