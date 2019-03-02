package com.haulmont.teamprogress.web.weekreport;

import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
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
@LoadDataBeforeShow
public class WeekReportBrowse extends MasterDetailScreen<WeekReport> {

    @Inject
    private CollectionLoader<Week> weeksDl;
    @Inject
    private CollectionContainer<Week> weeksDc;
    @Inject
    private InstanceContainer<WeekReport> weekReportDc;
    @Inject
    private LookupField<Week> weekField;
    @Inject
    private TextArea<String> plannedField;
    @Inject
    private TextArea<String> actualField;
    @Inject
    private DataManager dataManager;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Notifications notifications;
    @Inject
    private Dialogs dialogs;

    private boolean planEditable;
    private boolean actualEditable;
    private boolean weekEditable;
    @Inject
    private DataContext dataContext;

    @Subscribe
    private void onBeforeShow(BeforeShowEvent event) {
        weeksDl.setParameter("year", LocalDate.now().getYear());
    }

    @Subscribe
    private void onAfterShow(AfterShowEvent event) {
        weekEditable = weekField.isEditable();
        planEditable = plannedField.isEditable();
        actualEditable = actualField.isEditable();
    }

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);
        if (enabled) {
            WeekReport weekReport = weekReportDc.getItem();
            weekField.setEditable(Boolean.TRUE.equals(weekReport.getPlanEditable())
                    && Boolean.TRUE.equals(weekReport.getActEditable()) && weekEditable);
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
        Optional<Person> personOpt = dataManager.load(Person.class)
                .query("select p from tp_Person p where p.user = :user")
                .parameter("user", userSessionSource.getUserSession().getCurrentOrSubstitutedUser())
                .view("person-view")
                .optional();
        if (personOpt.isPresent()) {
            return personOpt.get();
        } else {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption("Cannot find current user in the list of people").show();
            throw new SilentException();
        }
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

}