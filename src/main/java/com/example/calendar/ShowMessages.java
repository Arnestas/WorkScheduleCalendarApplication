package com.example.calendar;

import com.example.calendar.dto.CalendarWithPlans;
import com.example.calendar.dto.InitialData;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Log4j2
public class ShowMessages {
    Scanner scanner = new Scanner(System.in);

    /**
     * The message is showed when the program starts.
     */
    public void sayHello(){
        System.out.println("\n * The program calculates whether the user will have enough time to complete the work (i.e., write a thesis).\n" +
                " * For that purpose, the user's plans are collected from the Google calendar,\n" +
                " * they are evaluated and it is calculated whether there will be enough time to complete the work.\n" +
                " * Finally, the program schedules how many hours each day needs to be worked to get the job done on time.");
    }

    /**
     * User enters the submission date (the last date).
     *
     * @return Entered date.
     */
    public LocalDate inputSubmissionDate() {
        boolean dateFormatIsCorrect = false;
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        while (!dateFormatIsCorrect) {
            System.out.println("\nWrite the work submission date in format yyyy-MM-dd and push Enter:");
            String dateString = scanner.next();
            log.info("Entered date: " + dateString);
            try {
                localDate = LocalDate.parse(dateString, dateFormat);
                dateFormatIsCorrect = true;
            } catch (DateTimeParseException e) {
                log.error("Error - bad date format: " + dateString);
            }
        }
        return localDate;
    }

    /**
     * User enters if sundays will be calculated in new calendar.
     *
     * @return boolean value
     */
    public boolean workOnSunday(){
        boolean answerFormatIsCorrect = false;
        boolean workOnSunday = false;
        String inputString;
        while (!answerFormatIsCorrect){
            System.out.println("\nDo you plan to work on Sundays?");
            System.out.println("Enter 'Y' or 'N'");
            inputString = scanner.next();
            if (inputString.toLowerCase(Locale.ROOT).equals("y")){
                workOnSunday = true;
                answerFormatIsCorrect = true;
            } else if (inputString.toLowerCase(Locale.ROOT).equals("n")){
                answerFormatIsCorrect = true;
            } else
                System.out.println("Answer format is not walid!");
        }
        return workOnSunday;
    }

    /**
     * Users enters how many hours he needs to finish the work.
     *
     * @return The amount of hours to finish the work.
     */
    public int hoursToCompleteTheWork(){
        int hours = 0;
        boolean answerFormatIsCorrect = false;
        while (!answerFormatIsCorrect){
            System.out.println("\nHow many hours do you need to finish the work?");
            hours = scanner.nextInt();
            if (hours > 0) {
                answerFormatIsCorrect = true;
            }
        }
        return hours;
    }

    /**
     * Prints list of CalendarWithPlans objects.
     *
     * @param calendarWithPlans List of objects.
     * @param message The message describes what was printed.
     */
    public void showList(List<CalendarWithPlans> calendarWithPlans, String message){
        System.out.println(message);
        for (CalendarWithPlans day : calendarWithPlans){
            System.out.println(day);
        }
    }

    /**
     * Prints list of InitialData objects.
     * @param plans List of objects.
     * @param message The message describes what was printed.
     */
    public void showList(ArrayList<InitialData> plans, String message){
        System.out.println(message);
        for (InitialData day : plans){
            System.out.println(day);
        }
    }

    /**
     * Prints messages about results: available hours to work and the hours needed to complete the work.
     * @param availableHoursToWork The total number of available hours to work.
     * @param hoursToCompleteTheWork The total number of hours needed to complete the work.
     * @param days The number of days available to work.
     */
    public void willFinnishTheWorkOntime(double availableHoursToWork, int hoursToCompleteTheWork, int days){
        System.out.println("\nYou have " + days + " days and " + availableHoursToWork + " possible hours to work.");
        System.out.println("You need " + hoursToCompleteTheWork + " hours to finish your work.");

        if (availableHoursToWork > hoursToCompleteTheWork) {
            System.out.println("You should finish your work at time.");
        } else System.out.println("You have to review your schedule!!");
    }

}