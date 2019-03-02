package com.haulmont.teamprogress.web.project;

import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.teamprogress.entity.Project;
import com.haulmont.teamprogress.entity.Team;

import javax.inject.Inject;

@UiController("tp_Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class ProjectBrowse extends MasterDetailScreen<Project> {

    @Inject
    private Table<Team> teamsTable;

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);
        teamsTable.getActions().forEach(action -> action.setEnabled(enabled));
    }
}