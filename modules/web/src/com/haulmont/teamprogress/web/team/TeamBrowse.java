package com.haulmont.teamprogress.web.team;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.teamprogress.entity.Team;

@UiController("tp_Team.browse")
@UiDescriptor("team-browse.xml")
@LookupComponent("teamsTable")
@LoadDataBeforeShow
public class TeamBrowse extends StandardLookup<Team> {
}