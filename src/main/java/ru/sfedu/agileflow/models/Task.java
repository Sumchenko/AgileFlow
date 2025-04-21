package ru.sfedu.agileflow.models;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

@Entity
@Table(name = "tasks")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute
    private int id;

    @XmlElement
    private String title;

    @XmlElement
    private String description;

    @Enumerated(EnumType.STRING)
    @XmlElement
    private TaskStatus status;

    @XmlElement
    private int priority;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @XmlElement
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    @XmlElement
    private User assignedUser;

    public Task() {
    }

    public Task(String title, String description, TaskStatus status, int priority, Sprint sprint, User assignedUser) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.sprint = sprint;
        this.assignedUser = assignedUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", sprintId=" + (sprint != null ? sprint.getId() : null) +
                ", assignedUserId=" + (assignedUser != null ? assignedUser.getId() : null) +
                '}';
    }
}