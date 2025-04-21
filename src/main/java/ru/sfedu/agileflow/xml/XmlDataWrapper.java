package ru.sfedu.agileflow.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import ru.sfedu.agileflow.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс-обертка для сериализации/десериализации коллекций сущностей в XML.
 */
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDataWrapper {

    @XmlElement(name = "project")
    private List<Project> projects = new ArrayList<>();

    @XmlElement(name = "user")
    private List<User> users = new ArrayList<>();

    @XmlElement(name = "sprint")
    private List<Sprint> sprints = new ArrayList<>();

    @XmlElement(name = "task")
    private List<Task> tasks = new ArrayList<>();

    @XmlElement(name = "retrospective")
    private List<Retrospective> retrospectives = new ArrayList<>();

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Retrospective> getRetrospectives() {
        return retrospectives;
    }

    public void setRetrospectives(List<Retrospective> retrospectives) {
        this.retrospectives = retrospectives;
    }
}