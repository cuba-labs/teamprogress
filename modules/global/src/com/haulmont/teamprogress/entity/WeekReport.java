package com.haulmont.teamprogress.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TP_WEEK_REPORT",
        uniqueConstraints = @UniqueConstraint(
                name = "TP_WEEK_REPORT_UNIQUE_WEEK_PERSON",
                columnNames = {"WEEK_ID", "PERSON_ID"}))
@Entity(name = "tp_WeekReport")
public class WeekReport extends StandardEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WEEK_ID")
    protected Week week;

    @Column(name = "PLANNED", length = 1000)
    protected String planned;

    @Column(name = "ACTUAL", length = 1000)
    protected String actual;

    @Column(name = "PLAN_EDITABLE")
    protected Boolean planEditable = true;

    @Column(name = "ACT_EDITABLE")
    protected Boolean actEditable = true;

    public Boolean getActEditable() { return actEditable; }

    public void setActEditable(Boolean actEditable) { this.actEditable = actEditable; }

    public Boolean getPlanEditable() { return planEditable; }

    public void setPlanEditable(Boolean planEditable) { this.planEditable = planEditable; }

    public Person getPerson() { return person; }

    public void setPerson(Person person) { this.person = person; }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getPlanned() {
        return planned;
    }

    public void setPlanned(String planned) {
        this.planned = planned;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }
}