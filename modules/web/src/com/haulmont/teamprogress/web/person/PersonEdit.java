package com.haulmont.teamprogress.web.person;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.teamprogress.entity.Person;

@UiController("tp_Person.edit")
@UiDescriptor("person-edit.xml")
@EditedEntityContainer("personDc")
@LoadDataBeforeShow
public class PersonEdit extends StandardEditor<Person> {
}