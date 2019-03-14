package com.haulmont.teamprogress.web.weekreport;

import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.teamprogress.entity.Person;
import com.haulmont.teamprogress.entity.Week;
import com.haulmont.teamprogress.entity.WeekReport;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@UiController("tp_WeekReport.browse")
@UiDescriptor("week-report-browse.xml")
@LookupComponent("table")
public class WeekReportBrowse extends MasterDetailScreen<WeekReport> {

    @Inject
    private CollectionLoader<Week> weeksDl;
    @Inject
    private CollectionContainer<Week> weeksDc;
    @Inject
    private InstanceContainer<WeekReport> weekReportDc;
    @Inject
    private CollectionLoader<WeekReport> weekReportsDl;
    @Inject
    private LookupField<Week> weekField;
    @Inject
    private TextArea<String> plannedField;
    @Inject
    private TextArea<String> actualField;
    @Inject
    private PopupButton actionsBtn;
    @Inject
    private Button prevWeekBtn;
    @Inject
    private Button nextWeekBtn;
    @Inject
    private Button currentWeekBtn;
    @Inject
    private LookupField<Week> fromWeekFilterField;
    @Inject
    private LookupField<Week> toWeekFilterField;
    @Inject
    private LookupField<Person> personFilterField;
    @Inject
    private GroupTable<WeekReport> table;

    @Inject
    private DataManager dataManager;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Notifications notifications;
    @Inject
    private Dialogs dialogs;
    @Inject
    private DataContext dataContext;

    private boolean planEditable;
    private boolean actualEditable;
    @Inject
    private CollectionLoader<Person> personsDl;

    @Subscribe
    private void onInit(InitEvent event) {
        if (!userSessionSource.getUserSession().isSpecificPermitted("tp.freezeWeeklyReport")) {
            actionsBtn.setVisible(false);
            actionsBtn.setEnabled(false);
        }
    }

    @Subscribe
    private void onBeforeShow(BeforeShowEvent event) {
        weeksDl.setParameter("year", LocalDate.now().getYear());

        weeksDl.load();
        personsDl.load();

        fromWeekFilterField.setValue(getRelativeWeek(-7));
        toWeekFilterField.setValue(getRelativeWeek(7));
        personFilterField.setValue(getCurrentPersonOrNull());
        loadWeekReports();

        fromWeekFilterField.addValueChangeListener(valueChangeEvent -> loadWeekReports());
        toWeekFilterField.addValueChangeListener(valueChangeEvent -> loadWeekReports());
        personFilterField.addValueChangeListener(valueChangeEvent -> loadWeekReports());
    }

    private void loadWeekReports() {
        weekReportsDl.setParameter("startWeek", getWeekNumberOrNull(fromWeekFilterField.getValue()));
        weekReportsDl.setParameter("endWeek", getWeekNumberOrNull(toWeekFilterField.getValue()));
        weekReportsDl.setParameter("person", personFilterField.getValue());
        weekReportsDl.load();
    }

    @Nullable
    private Integer getWeekNumberOrNull(@Nullable Week week) {
        return week != null ? week.getWeekNumber() : null;
    }

    @Subscribe
    private void onAfterShow(AfterShowEvent event) {
        table.expandAll();
        planEditable = plannedField.isEditable();
        actualEditable = actualField.isEditable();
    }

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);
        weekField.setEditable(enabled);
        prevWeekBtn.setVisible(enabled);
        nextWeekBtn.setVisible(enabled);
        currentWeekBtn.setVisible(enabled);
        if (enabled) {
            WeekReport weekReport = weekReportDc.getItem();
            weekField.setEditable(Boolean.TRUE.equals(weekReport.getPlanEditable())
                    && Boolean.TRUE.equals(weekReport.getActEditable()));
            plannedField.setEditable(Boolean.TRUE.equals(weekReport.getPlanEditable()) && planEditable);
            actualField.setEditable(Boolean.TRUE.equals(weekReport.getActEditable()) && actualEditable);
        }
    }

    @Subscribe
    private void onInitEntity(InitEntityEvent<WeekReport> event) {
        try {
            event.getEntity().setPerson(getCurrentPerson());
            event.getEntity().setWeek(getCurrentWeek());
        } catch (Exception e) {
            dataContext.evict(event.getEntity());
        }
    }

    private Person getCurrentPerson() {
        Person person = getCurrentPersonOrNull();
        if (person != null) {
            return person;
        } else {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption("Cannot find current user in the list of people").show();
            throw new SilentException();
        }
    }

    @Nullable
    private Person getCurrentPersonOrNull() {
        return dataManager.load(Person.class)
                .query("select p from tp_Person p where p.user = :user")
                .parameter("user", userSessionSource.getUserSession().getCurrentOrSubstitutedUser())
                .view("person-view")
                .optional()
                .orElse(null);
    }

    @Nullable
    private Week getRelativeWeek(int offsetDays) {
        return weeksDc.getItems().stream()
                .filter(week ->
                        (LocalDate.now().getDayOfYear() + offsetDays) >= week.getStartDate().getDayOfYear()
                            && (LocalDate.now().getDayOfYear() + offsetDays) <= week.getEndDate().getDayOfYear())
                .findFirst()
                .orElse(null);
    }

    @Nullable
    private Week getCurrentWeek() {
        return weeksDc.getItems().stream()
                .filter(week ->
                        LocalDate.now().getDayOfYear() >= week.getStartDate().getDayOfYear()
                            && LocalDate.now().getDayOfYear() <= week.getEndDate().getDayOfYear())
                .findFirst()
                .orElse(null);
    }

    @Subscribe("actionsBtn.freezePlan")
    private void onActionsBtnFreezePlan(Action.ActionPerformedEvent event) {
        doWithSelectedRecords("freeze plan", weekReport -> weekReport.setPlanEditable(false));
    }

    @Subscribe("actionsBtn.unfreezePlan")
    private void onActionsBtnUnfreezePlan(Action.ActionPerformedEvent event) {
        doWithSelectedRecords("unfreeze plan", weekReport -> weekReport.setPlanEditable(true));
    }

    @Subscribe("actionsBtn.freezeActual")
    private void onActionsBtnFreezeActual(Action.ActionPerformedEvent event) {
        doWithSelectedRecords("freeze actual", weekReport -> weekReport.setActEditable(false));
    }

    @Subscribe("actionsBtn.unfreezeActual")
    private void onActionsBtnUnfreezeActual(Action.ActionPerformedEvent event) {
        doWithSelectedRecords("unfreeze actual", weekReport -> weekReport.setActEditable(true));
    }

    private void doWithSelectedRecords(String action, Consumer<WeekReport> consumer) {
        Set<WeekReport> selected = getTable().getSelected();
        if (selected.isEmpty()) {
            notifications.create().withCaption("No records selected").show();
            return;
        }
        dialogs.createOptionDialog()
                .withCaption("Please confirm")
                .withMessage(String.format("Are you sure you wan to %s for selected records?", action))
                .withActions(
                        new DialogAction(DialogAction.Type.OK).withHandler(actionPerformedEvent -> {
                            CommitContext commitContext = new CommitContext();
                            for (WeekReport weekReport : selected) {
                                WeekReport reloadedInstance = dataManager.load(Id.of(weekReport)).one();
                                consumer.accept(reloadedInstance);
                                commitContext.addInstanceToCommit(reloadedInstance);
                            }
                            dataManager.commit(commitContext);
                        }),
                        new DialogAction(DialogAction.Type.CANCEL)
                )
                .show();
    }

    @Subscribe("nextWeekBtn")
    private void onNextWeekBtnClick(Button.ClickEvent event) {
        Week week = weekField.getValue();
        if (week == null) {
            weekField.setValue(getCurrentWeek());
        } else {
            int index = weeksDc.getItemIndex(week);
            if (index < weeksDc.getItems().size() - 1) {
                weekField.setValue(weeksDc.getItems().get(index + 1));
            }
        }
    }

    @Subscribe("prevWeekBtn")
    private void onPrevWeekBtnClick(Button.ClickEvent event) {
        Week week = weekField.getValue();
        if (week == null) {
            weekField.setValue(getCurrentWeek());
        } else {
            int index = weeksDc.getItemIndex(week);
            if (index > 0) {
                weekField.setValue(weeksDc.getItems().get(index - 1));
            }
        }
    }

    @Subscribe("currentWeekBtn")
    private void onCurrentWeekBtnClick(Button.ClickEvent event) {
        weekField.setValue(getCurrentWeek());
    }
}