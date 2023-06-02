package com.example.calendar;

import com.example.calendar.dto.CalendarWithPlans;
import com.example.calendar.dto.InitialData;
import com.google.api.services.calendar.model.Event;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The program calculates whether the user will have enough time to complete the work (i.e., write a thesis).
 * For that purpose, the user's plans are collected from the Google Calendar,
 * they are evaluated, and it is calculated whether there will be enough time to complete the work.
 * Finally, the program schedules how many hours each day needs to be worked to get the job done on time.
 */

@SpringBootApplication
public class WorkScheduleCalendarApplication {

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(WorkScheduleCalendarApplication.class, args);

		CollectCalendarData workWithCalendarData = new CollectCalendarData();
		CalendarCalculations calendarCalculations = new CalendarCalculations();
		ShowMessages showMessages = new ShowMessages();

		showMessages.sayHello();
		LocalDate submissionDate = showMessages.inputSubmissionDate();
		boolean workOnSunday = showMessages.workOnSunday();
		int hoursToCompleteTheWork = showMessages.hoursToCompleteTheWork();

		List<Event> events = workWithCalendarData.getDataFromCalendar(submissionDate);

		ArrayList<InitialData> plans = workWithCalendarData.makePlans(events);
		showMessages.showList(plans, "\nList of plans from the calendar: ");

		List<CalendarWithPlans> calendarWithPlans = calendarCalculations.generateNewCalendarWithPlans(plans, submissionDate, workOnSunday, hoursToCompleteTheWork);
		showMessages.showList(calendarWithPlans, "\nNew calendar with plans: ");

		calendarCalculations.calculateWorkLoadSituation(calendarWithPlans);

		calendarCalculations.generateWorkCalendar(calendarWithPlans, hoursToCompleteTheWork);
	}
}