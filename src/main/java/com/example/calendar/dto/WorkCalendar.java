package com.example.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkCalendar {
    private LocalDate date;
    private double hoursPlanned;
    private double hoursToWork;
    private DayOfWeek dayOfWeek;
}
