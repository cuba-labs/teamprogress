package com.haulmont.teamprogress.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NamePattern("#getCaption|weekNumber,startDate,endDate")
@Table(name = "TP_WEEK")
@Entity(name = "tp_Week")
public class Week extends StandardEntity {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");

    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected LocalDate startDate;

    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected LocalDate endDate;

    @Column(name = "WEEK_NUMBER")
    protected Integer weekNumber;

    @NumberFormat(pattern = "#")
    @NotNull
    @Column(name = "YEAR_", nullable = false)
    protected Integer year;

    public String getCaption() {
        return String.format("%d#%d %s-%s",
                startDate.getYear(), weekNumber, startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}