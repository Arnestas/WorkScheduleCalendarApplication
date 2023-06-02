package com.example.calendar;

import com.example.calendar.dto.CalendarWithPlans;
import com.example.calendar.dto.InitialData;
import lombok.extern.log4j.Log4j2;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CalendarCalculations {
    ShowMessages showMessages = new ShowMessages();
    public final int HOURS_PER_DAY = 24;
    public final int SLEEPING_HOURS = 8;

    /**
     * Generates a new calendar with plans based on the provided parameters.
     *
     * @param plans List of objects of InitialData.
     * @param submissionDate The submission date up to which the calendar should be generated.
     * @param workOnSunday A boolean value indicates if work is allowed on Sundays.
     * @param hoursToCompleteTheWork The total number of hours required to complete the work.
     * @return A list of CalendarWithPlans representing the generated calendar with plans.
     */
    public List<CalendarWithPlans> generateNewCalendarWithPlans(List<InitialData> plans, LocalDate submissionDate, boolean workOnSunday, int hoursToCompleteTheWork){
        List<CalendarWithPlans> calendarWithPlans = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        double plannedHoursOfTheDay;
        double allPlannedHours = 0;
        while (!currentDate.isAfter(submissionDate)) {
            if (!workOnSunday && currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                currentDate = currentDate.plusDays(1);
            }
            plannedHoursOfTheDay = sumEventsDurationInDay(currentDate, plans);
            calendarWithPlans.add(new CalendarWithPlans(currentDate, plannedHoursOfTheDay, 0, currentDate.getDayOfWeek()));
            allPlannedHours += plannedHoursOfTheDay;
            currentDate = currentDate.plusDays(1);
        }
        double availableHoursToWork = (HOURS_PER_DAY - SLEEPING_HOURS) * calendarWithPlans.size() - allPlannedHours;
        showMessages.willFinnishTheWorkOntime(availableHoursToWork, hoursToCompleteTheWork, calendarWithPlans.size());

        return calendarWithPlans;
    }

    /**
     * Calculates the total duration of events for a given date.
     *
     * @param currentDate The date for which to calculate the total duration of events.
     * @param plans List of objects of InitialData representing the plans.
     * @return The total duration of events for the given date.
     */
    public double sumEventsDurationInDay(LocalDate currentDate, List<InitialData> plans){
        double busyHoursOfTheDay = 0;
        for (InitialData plan : plans){
            if(currentDate.isEqual(plan.getDate())){
                busyHoursOfTheDay = busyHoursOfTheDay + plan.getDuration();
            }
        }
        return (busyHoursOfTheDay > 0) ? busyHoursOfTheDay : 0;
    }

    /**
     * Generates a work calendar based on the provided list of CalendarWithPlans
     * and the total hours needed to complete the work.
     *
     * @param calendarWithPlans The list representing the calendar with planned hours for each day.
     * @param hoursToCompleteTheWork The total number of hours needed to complete the work.
     */
    public void generateWorkCalendar(List<CalendarWithPlans> calendarWithPlans, int hoursToCompleteTheWork){
        int hoursToWorkInDay;
        int calculatedWorkedHours = 0;
        int hoursLeftToCompleteTheWork = hoursToCompleteTheWork;
        int dailyWork;
        for (int i = 0; i < calendarWithPlans.size(); i++){
            hoursToWorkInDay = hoursLeftToCompleteTheWork / ((calendarWithPlans.size()-i)); // we will work by average hours per day
            dailyWork = (int)(HOURS_PER_DAY - SLEEPING_HOURS - calendarWithPlans.get(i).getHoursPlanned()) - hoursToWorkInDay;
            if (dailyWork < 0){
                hoursToWorkInDay = (int)(HOURS_PER_DAY - SLEEPING_HOURS - calendarWithPlans.get(i).getHoursPlanned());
            }
            if (hoursToWorkInDay < 0){
                hoursToWorkInDay = 0;
            }
            calendarWithPlans.get(i).setHoursToWork(hoursToWorkInDay);
            hoursLeftToCompleteTheWork -= hoursToWorkInDay;
            calculatedWorkedHours += hoursToWorkInDay;
            System.out.printf("Hours to work %s, workedHours %s, %s hours to complete the work \n",hoursToWorkInDay, calculatedWorkedHours, hoursLeftToCompleteTheWork);
            if(calculatedWorkedHours >= hoursToCompleteTheWork) {
                break;
            }
        }
        showMessages.showList(calendarWithPlans, "\nCalculated worked hours: " + calculatedWorkedHours);
    }

    /**
     * Checks if there is enough time to sleep and to finish plans in a day.
     *
     * @param calendarWithPlans The list representing the calendar with planned hours for each day.
     */
    public void calculateWorkLoadSituation(List<CalendarWithPlans> calendarWithPlans){
        List<CalendarWithPlans> workLoadCalendar = new ArrayList<>();
        int workLoadSituations = 0;
        for (CalendarWithPlans day : calendarWithPlans){
            if ((HOURS_PER_DAY - SLEEPING_HOURS - day.getHoursPlanned()<0)){
                workLoadSituations += 1;
                workLoadCalendar.add(day);
            }
        }
        if (workLoadSituations > 0){
            ShowMessages showMessages = new ShowMessages();
            showMessages.showList(workLoadCalendar, "\nYour schedule has impossible situation!! You have "+ workLoadSituations + " overloaded day(s):");
        }
    }

}