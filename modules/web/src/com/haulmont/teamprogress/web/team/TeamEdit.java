package com.haulmont.teamprogress.web.team;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.teamprogress.entity.Team;

@UiController("tp_Team.edit")
@UiDescriptor("team-edit.xml")
@EditedEntityContainer("teamDc")
@LoadDataBeforeShow
public class TeamEdit extends StandardEditor<Team> {
}