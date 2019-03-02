package com.haulmont.teamprogress.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "TP_PROJECT")
@Entity(name = "tp_Project")
public class Project extends StandardEntity {
    @Column(name = "NAME")
    protected String name;

    @Composition
    @OrderBy("name")
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    protected List<Team> teams;

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}