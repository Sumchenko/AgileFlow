package ru.sfedu.agileflow.lab3.singletable;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import ru.sfedu.agileflow.models.TaskStatus;

import java.util.Objects;

/**
 * Базовый класс для задач, использующий стратегию Single Table для наследования.
 */
@Entity
@Table(name = "tasks_single_table")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Generic")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Task {
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

    @XmlElement
    private Integer sprintId;

    @XmlElement
    private Integer assignedUserId;

    public Task() {
    }

    public Task(String title, String description, TaskStatus status, int priority, Integer sprintId, Integer assignedUserId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.sprintId = sprintId;
        this.assignedUserId = assignedUserId;
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

    public Integer getSprintId() {
        return sprintId;
    }

    public void setSprintId(Integer sprintId) {
        this.sprintId = sprintId;
    }

    public Integer getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Integer assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", sprintId=" + sprintId +
                ", assignedUserId=" + assignedUserId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}