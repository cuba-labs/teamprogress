package com.haulmont.teamprogress.web.week;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.teamprogress.entity.Week;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@UiController("tp_Week.browse")
@UiDescriptor("week-browse.xml")
@LookupComponent("weeksTable")
@LoadDataBeforeShow
public class WeekBrowse extends StandardLookup<Week> {

    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private CollectionLoader<Week> weeksDl;

    @Subscribe("weeksTable.generate")
    private void onWeeksTableGenerate(Action.ActionPerformedEvent event) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfYear = LocalDate.of(now.getYear(), 1, 1);
        LocalDate lastDayOfYear = LocalDate.of(now.getYear(), 12, 31);
        List<Week> existingWeeks = dataManager.load(Week.class)
                .query("select w from tp_Week w where w.year = :year")
                .parameter("year", now.getYear())
                .list();
        LocalDate day = firstDayOfYear;
        int weekNum = 1;
        while (day.isBefore(lastDayOfYear) || day.isEqual(lastDayOfYear)) {
            if ((weekNum == 1 || day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                    && weekDoesNotExist(existingWeeks, day)) {
                Week week = metadata.create(Week.class);
                week.setYear(now.getYear());
                week.setWeekNumber(weekNum);
                week.setStartDate(day);
                while (!day.getDayOfWeek().equals(DayOfWeek.SUNDAY) && day.isBefore(lastDayOfYear)) {
                    day = day.plusDays(1);
                }
                week.setEndDate(day);
                dataManager.commit(week);
                weekNum++;
            }
            day = day.plusDays(1);
        }

        weeksDl.load();
    }

    private boolean weekDoesNotExist(List<Week> existingWeeks, LocalDate day) {
        return existingWeeks.stream().noneMatch(week -> week.getStartDate().equals(day));
    }

}