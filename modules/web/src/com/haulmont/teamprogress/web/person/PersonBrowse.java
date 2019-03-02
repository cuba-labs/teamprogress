package com.haulmont.teamprogress.web.person;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.teamprogress.entity.Person;

@UiController("tp_Person.browse")
@UiDescriptor("person-browse.xml")
@LookupComponent("personsTable")
@LoadDataBeforeShow
public class PersonBrowse extends StandardLookup<Person> {
}